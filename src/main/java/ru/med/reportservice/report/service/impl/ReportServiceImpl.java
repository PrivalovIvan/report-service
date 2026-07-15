package ru.med.reportservice.report.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.med.reportservice.generator.ReportGenerator;
import ru.med.reportservice.generator.excel.dto.ExcelRow;
import ru.med.reportservice.generator.excel.dto.Level;
import ru.med.reportservice.report.dto.ReportDataDto;
import ru.med.reportservice.report.repository.ReportRepository;
import ru.med.reportservice.report.service.ReportService;
import ru.med.reportservice.report.service.dto.MkbKey;
import ru.med.reportservice.report.service.dto.MoKey;
import ru.med.reportservice.report.service.dto.SmoKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Реализация сервиса генерации отчёта.
 * Получает данные из репозитория, строит иерархию строк и делегирует генерацию файла
 * компоненту ReportGenerator.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final ReportGenerator<ExcelRow> excelReportGenerator;

    @Override
    @Transactional(readOnly = true)
    public byte[] generateReport() {
        log.info("Loading report data...");
        List<ReportDataDto> reportData = reportRepository.getReportData();
        log.info("Loaded {} records.", reportData.size());

        List<ExcelRow> excelRows = buildRows(reportData);
        return excelReportGenerator.generate(excelRows);
    }

    /**
     * Преобразует список сырых данных в иерархический список строк Excel.
     * Группирует по СМО → МО → МКБ и подсчитывает количество случаев.
     *
     * @param data список записей из БД с расшифрованными наименованиями
     * @return список строк для Excel
     */
    private List<ExcelRow> buildRows(List<ReportDataDto> data) {
        Map<SmoKey, Map<MoKey, Map<MkbKey, Long>>> report = data.stream()
            .collect(Collectors.groupingBy(r -> new SmoKey(r.smoCode(), r.smoName()),
                Collectors.groupingBy(r -> new MoKey(r.moCode(), r.moName()),
                    Collectors.groupingBy(r -> new MkbKey(r.mkbCode(), r.mkbName()), Collectors.counting()))
            ));

        List<ExcelRow> rows = new ArrayList<>();
        rows.add(new ExcelRow(Level.TOTAL, "", "Итого", data.size()));

        for (var smoEntry : report.entrySet()) {
            SmoKey smo = smoEntry.getKey();
            Map<MoKey, Map<MkbKey, Long>> moMap = smoEntry.getValue();
            long smoCount = moMap.values().stream()
                .flatMap(m -> m.values().stream())
                .mapToLong(Long::longValue)
                .sum();

            rows.add(new ExcelRow(Level.SMO, String.valueOf(smo.code()), smo.name(), smoCount));

            for (var moEntry : moMap.entrySet()) {
                MoKey mo = moEntry.getKey();
                Map<MkbKey, Long> mkbMap = moEntry.getValue();

                long moCount = mkbMap.values().stream().mapToLong(Long::longValue).sum();

                rows.add(new ExcelRow(Level.MO, String.valueOf(mo.code()), mo.name(), moCount));

                for (var mkbEntry : mkbMap.entrySet()) {
                    MkbKey mkb = mkbEntry.getKey();
                    rows.add(new ExcelRow(Level.MKB, mkb.code(), mkb.name(), mkbEntry.getValue()));
                }
            }
        }

        return rows;
    }
}
