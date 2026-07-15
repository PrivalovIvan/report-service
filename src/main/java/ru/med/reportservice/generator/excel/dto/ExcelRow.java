package ru.med.reportservice.generator.excel.dto;

/**
 * Строка отчёта для вывода в Excel.
 * @param level уровень иерархии (0 — Итого, 1 — СМО, 2 — МО, 3 — МКБ)
 * @param code код (для СМО/МО/МКБ, для итога — пустая строка)
 * @param name наименование
 * @param count количество случаев
 */
public record ExcelRow(
    Level level,
    String code,
    String name,
    long count
) {
}
