package ru.med.reportservice.report.dto;

/**
 * DTO для передачи данных из репозитория в сервис.
 * Содержит коды и наименования СМО, МО, МКБ.
 */
public record ReportDataDto(
    int smoCode,
    String smoName,

    int moCode,
    String moName,

    String mkbCode,
    String mkbName
) {
}
