package ru.med.reportservice.generator.excel.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import ru.med.reportservice.generator.ReportGenerator;
import ru.med.reportservice.generator.excel.dto.ExcelRow;
import ru.med.reportservice.generator.excel.dto.Level;
import ru.med.reportservice.generator.excel.exception.ExcelGeneratedException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Генератор отчётов в формате Excel (.xlsx).
 * Создаёт иерархический отчёт с цветовой маркировкой строк и автоматической шириной колонок.
 */
@Slf4j
@Service
public class ExcelReportGenerator implements ReportGenerator<ExcelRow> {
    private static final int COLUMN_COUNT = 4;
    private static final int LAST_COLUMN_INDEX = COLUMN_COUNT - 1;

    private static final String[] COLUMNS = {"Тип строки", "Код", "Наименование", "Количество случаев"};

    /**
     * Генерирует Excel-файл на основе списка строк отчёта.
     *
     * @param rows строки отчёта (уже сгруппированные и отсортированные)
     * @return массив байтов готового .xlsx-файла
     * @throws ExcelGeneratedException    если произошла ошибка ввода-вывода
     */
    @Override
    public byte[] generate(List<ExcelRow> rows) {
        EnumMap<Level, CellStyle> styleCache = new EnumMap<>(Level.class);

        try (
            Workbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ) {
            Sheet sheet = workbook.createSheet("Report");
            sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, LAST_COLUMN_INDEX));

            createHeader(workbook, sheet);
            fillDataRows(workbook, sheet, rows, styleCache);

            for (int i = 0; i < COLUMN_COUNT; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);

            byte[] result = outputStream.toByteArray();
            log.info("Excel generated, size: {} bytes", result.length);

            return result;
        } catch (IOException e) {
            log.error("Ошибка при создании Excel", e);
            throw new ExcelGeneratedException("Ошибка генерации Excel: ", e);
        }
    }

    /**
     * Создаёт строку заголовка.
     */
    private void createHeader(Workbook workbook, Sheet sheet) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();

        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);

        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < COLUMNS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(COLUMNS[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    /**
     * Заполняет строки данных и применяет стили.
     */
    private void fillDataRows(Workbook workbook, Sheet sheet, List<ExcelRow> rows, Map<Level, CellStyle> styleCache) {
        int rowNum = 1;

        for (ExcelRow excelRow : rows) {
            Row row = createDataRow(sheet, rowNum++, excelRow);

            CellStyle rowStyle = getRowStyle(workbook, excelRow.level(), styleCache);

            for (int i = 0; i < COLUMN_COUNT; i++) {
                row.getCell(i).setCellStyle(rowStyle);
            }
        }
    }

    /**
     * Возвращает стиль для уровня, используя локальный кэш.
     */
    private CellStyle getRowStyle(Workbook workbook, Level level, Map<Level, CellStyle> styleCache) {

        return styleCache.computeIfAbsent(level, l -> {
            CellStyle style = workbook.createCellStyle();

            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);

            switch (l) {
                case TOTAL -> style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
                case SMO -> style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
                case MO -> style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
                default -> style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
            }
            return style;
        });
    }

    /**
     * Создаёт одну строку данных.
     */
    private Row createDataRow(Sheet sheet, int rowNum, ExcelRow excelRow) {
        Row row = sheet.createRow(rowNum);

        row.createCell(0).setCellValue(excelRow.level().ordinal());
        row.createCell(1).setCellValue(excelRow.code());
        row.createCell(2).setCellValue(excelRow.name());
        row.createCell(3).setCellValue(excelRow.count());

        return row;
    }
}
