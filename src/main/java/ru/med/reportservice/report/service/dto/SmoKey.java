package ru.med.reportservice.report.service.dto;

/**
 * Ключ группировки для СМО.
 * @param code код СМО
 * @param name наименование СМО
 */
public record SmoKey(
    int code,
    String name
) {
}
