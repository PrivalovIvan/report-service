package ru.med.reportservice.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.med.reportservice.generator.GeneratorException;

/**
 * Глобальный обработчик исключений для всего приложения.
 * Перехватывает ошибки генерации отчётов и другие исключения,
 * возвращая понятный ответ клиенту.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключения, связанные с генерацией отчётов.
     * @param e исключение генератора
     * @return ответ с кодом 500 и описанием ошибки
     */
    @ExceptionHandler(GeneratorException.class)
    public ResponseEntity<String> handleGeneratorException(GeneratorException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Ошибка генерации отчёта: " + e.getMessage());
    }

    /**
     * Обрабатывает непредвиденные исключения.
     * @param e исключение
     * @return ответ с кодом 500 и общим сообщением
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Внутренняя ошибка сервера: " + e.getMessage());
    }
}
