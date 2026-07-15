package ru.med.reportservice.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Утилитарный класс для работы с ZIP-архивами.
 */
@Slf4j
@UtilityClass
public class ZipUtil {

    /**
     * Упаковывает переданный массив байтов в ZIP-архив с заданным именем файла.
     *
     * @param fileName имя файла внутри архива (например, "report.xlsx")
     * @param content  содержимое файла в виде байтового массива
     * @return массив байтов ZIP-архива
     * @throws IllegalArgumentException если fileName или content равны null
     * @throws RuntimeException        если произошла ошибка ввода-вывода при создании архива
     */
    public byte[] zipFile(String fileName, byte[] content) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("Имя файла не может быть пустым");
        }
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("Содержимое файла не может быть пустым");
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipEntry entry = new ZipEntry(fileName);
            zos.putNextEntry(entry);
            zos.write(content);
            zos.closeEntry();
            zos.finish();

            byte[] result = baos.toByteArray();
            log.info("ZIP archive created, size: {} bytes", result.length);
            return result;
        } catch (IOException e) {
            log.error("Ошибка создания ZIP-архива", e);
            throw new RuntimeException("Ошибка создания ZIP-архива", e);
        }
    }
}
