package ru.med.reportservice.generator.excel.impl;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.med.reportservice.generator.excel.dto.ExcelRow;
import ru.med.reportservice.generator.excel.dto.Level;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExcelReportGeneratorTest {

    private ExcelReportGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new ExcelReportGenerator();
    }

    @Test
    void generate_shouldCreateExcelWithCorrectStructure() throws IOException {
        // given
        List<ExcelRow> rows = List.of(
            new ExcelRow(Level.TOTAL, "", "Итого", 100),
            new ExcelRow(Level.SMO, "1", "СМО-1", 60),
            new ExcelRow(Level.MO, "10", "МО-1", 30),
            new ExcelRow(Level.MKB, "A00", "Холера", 10),
            new ExcelRow(Level.MKB, "A01", "Брюшной тиф", 20),
            new ExcelRow(Level.MO, "20", "МО-2", 30),
            new ExcelRow(Level.MKB, "B00", "Герпес", 30),
            new ExcelRow(Level.SMO, "2", "СМО-2", 40),
            new ExcelRow(Level.MO, "30", "МО-3", 40),
            new ExcelRow(Level.MKB, "C00", "Рак", 40)
        );

        // when
        byte[] excelBytes = generator.generate(rows);

        // then
        assertThat(excelBytes).isNotEmpty();

        // Проверяем содержимое Excel
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
            Sheet sheet = workbook.getSheetAt(0);
            assertThat(sheet).isNotNull();

            // Проверяем заголовок
            Row headerRow = sheet.getRow(0);
            assertThat(headerRow).isNotNull();
            assertThat(headerRow.getCell(0).getStringCellValue()).isEqualTo("Тип строки");
            assertThat(headerRow.getCell(1).getStringCellValue()).isEqualTo("Код");
            assertThat(headerRow.getCell(2).getStringCellValue()).isEqualTo("Наименование");
            assertThat(headerRow.getCell(3).getStringCellValue()).isEqualTo("Количество случаев");

            // Проверка количества строк данных
            int expectedRows = rows.size() + 1; // +1 для заголовка
            assertThat(sheet.getPhysicalNumberOfRows()).isEqualTo(expectedRows);

            // Проверка итоговой строки
            Row totalRow = sheet.getRow(1);
            assertThat(totalRow.getCell(0).getNumericCellValue()).isEqualTo(0); // Level.TOTAL.ordinal()
            assertThat(totalRow.getCell(1).getStringCellValue()).isEmpty();
            assertThat(totalRow.getCell(2).getStringCellValue()).isEqualTo("Итого");
            assertThat(totalRow.getCell(3).getNumericCellValue()).isEqualTo(100);

            // Проверка цвета фона итоговой строки
            CellStyle totalStyle = totalRow.getCell(0).getCellStyle();
            assertThat(totalStyle.getFillForegroundColor()).isEqualTo(IndexedColors.LIGHT_BLUE.getIndex());

            // Проверка строки СМО (индекс 2)
            Row smoRow = sheet.getRow(2);
            assertThat(smoRow.getCell(0).getNumericCellValue()).isEqualTo(1); // Level.SMO.ordinal()
            CellStyle smoStyle = smoRow.getCell(0).getCellStyle();
            assertThat(smoStyle.getFillForegroundColor()).isEqualTo(IndexedColors.LIGHT_GREEN.getIndex());

            // Проверка строки МКБ (индекс 4) — белый цвет
            Row mkbRow = sheet.getRow(4);
            CellStyle mkbStyle = mkbRow.getCell(0).getCellStyle();
            assertThat(mkbStyle.getFillForegroundColor()).isEqualTo(IndexedColors.WHITE.getIndex());
        }
    }
}
