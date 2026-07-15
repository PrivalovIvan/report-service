package ru.med.reportservice.report.service;

/**
 * Интерфейс сервиса для генерации отчёта.
 */
public interface ReportService {

    /**
     * Генерирует отчёт и возвращает его в виде байтового массива.
     * @return массив байтов готового файла
     */
    byte[] generateReport();
}
