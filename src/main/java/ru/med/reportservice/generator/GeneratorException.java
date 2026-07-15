package ru.med.reportservice.generator;

/**
 * Базовое исключение для ошибок генерации отчётов.
 */
public class GeneratorException extends RuntimeException {
    public GeneratorException(String message) {
        super(message);
    }

    public GeneratorException(String message, Throwable cause) {
        super(message, cause);
    }
}
