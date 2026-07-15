package ru.med.reportservice.report.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.med.reportservice.report.service.ReportService;

import java.io.ByteArrayInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    @Test
    void downloadReportFile_shouldReturnExcelFile() throws Exception {
        byte[] mockFile = "test excel content".getBytes();
        when(reportService.generateReport()).thenReturn(mockFile);

        mockMvc.perform(get("/api/v1/report/download/file"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
            .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.xlsx"))
            .andExpect(content().bytes(mockFile));
    }

    @Test
    void downloadReportZip_shouldReturnZipFile() throws Exception {
        byte[] mockFile = "test excel content".getBytes();
        when(reportService.generateReport()).thenReturn(mockFile);

        byte[] responseBytes = mockMvc.perform(get("/api/v1/report/download/zip"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
            .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.zip"))
            .andReturn()
            .getResponse()
            .getContentAsByteArray();

        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(responseBytes))) {
            ZipEntry entry = zis.getNextEntry();
            assertThat(entry).isNotNull();
            assertThat(entry.getName()).isEqualTo("report.xlsx");

            byte[] extracted = zis.readAllBytes();
            assertThat(extracted).isEqualTo(mockFile);
        }
    }
}
