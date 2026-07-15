package ru.med.reportservice.report.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.med.reportservice.report.service.ReportService;
import ru.med.reportservice.util.ZipUtil;

/**
 * REST-контроллер для скачивания отчёта.
 * Предоставляет эндпоинт для получения Excel-файла и его ZIP-архива.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * Скачивает отчёт в формате Excel.
     * @return HTTP-ответ с файлом report.xlsx
     */
    @GetMapping(value = "/report/download/file", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadReportFile() {
        log.info("Downloading report file");
        byte[] reportBytes = reportService.generateReport();

        return ResponseEntity.status(HttpStatus.OK)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.xlsx")
            .body(reportBytes);
    }

    /**
     * Скачивает архив с отчётом в формате Excel.
     * @return HTTP-ответ с архивом report.zip внутри которого report.xlsx
     */
    @GetMapping(value = "/report/download/zip", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadReportZip() {
        log.info("Downloading report as ZIP archive");

        byte[] reportBytes = reportService.generateReport();

        byte[] zipBytes = ZipUtil.zipFile("report.xlsx", reportBytes);

        return ResponseEntity.status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.zip")
            .body(zipBytes);
    }
}
