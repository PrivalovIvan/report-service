package ru.med.reportservice.report.repository;

import ru.med.reportservice.report.dto.ReportDataDto;

import java.util.List;

/**
 * Интерфейс репозитория для получения данных отчёта.
 */
public interface ReportRepository {
    /**
     * Возвращает список записей (случаев лечения) с расшифрованными кодами и наименованиями.
     * @return список объектов ReportDataDto
     */
    List<ReportDataDto> getReportData();
}
