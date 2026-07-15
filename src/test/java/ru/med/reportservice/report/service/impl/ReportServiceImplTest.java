package ru.med.reportservice.report.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.med.reportservice.generator.ReportGenerator;
import ru.med.reportservice.generator.excel.dto.ExcelRow;
import ru.med.reportservice.report.dto.ReportDataDto;
import ru.med.reportservice.report.repository.ReportRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ReportGenerator<ExcelRow> excelReportGenerator;

    @InjectMocks
    private ReportServiceImpl reportService;

    private List<ReportDataDto> testData;

    @BeforeEach
    void setUp() {
        testData = List.of(
            new ReportDataDto(1, "СМО-1", 10, "МО-1", "A00", "Холера"),
            new ReportDataDto(1, "СМО-1", 10, "МО-1", "A01", "Брюшной тиф"),
            new ReportDataDto(1, "СМО-1", 20, "МО-2", "B00", "Герпес"),
            new ReportDataDto(2, "СМО-2", 30, "МО-3", "C00", "Рак")
        );
    }

    @Test
    void generateReport_shouldBuildCorrectHierarchyAndCallGenerator() {
        // given
        when(reportRepository.getReportData()).thenReturn(testData);
        when(excelReportGenerator.generate(anyList())).thenReturn(new byte[]{1, 2, 3});

        // when
        byte[] result = reportService.generateReport();

        // then
        assertThat(result).isNotEmpty();
        verify(reportRepository).getReportData();
        verify(excelReportGenerator).generate(anyList());
    }
}
