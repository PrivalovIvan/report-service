package ru.med.reportservice.generator.excel.exception;

import ru.med.reportservice.generator.GeneratorException;

/**
 * Исключение, возникающее при ошибке создания Excel-файла.
 */
public class ExcelGeneratedException extends GeneratorException {
    public ExcelGeneratedException(String message, Throwable cause) {
        super(message, cause);
    }
}
