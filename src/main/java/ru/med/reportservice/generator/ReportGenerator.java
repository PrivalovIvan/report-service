package ru.med.reportservice.generator;

import java.util.List;

/**
 * Общий интерфейс для генераторов отчётов.
 * @param <T> тип элемента строки отчёта
 */
public interface ReportGenerator<T> {

    /**
     * Генерирует отчёт в виде массива байтов.
     * @param rows список строк отчёта
     * @return массив байтов готового файла
     */
    byte[] generate(List<T> rows);
}
