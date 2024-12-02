package io.github.luidmidev.springframework.data.crud.core.utils;

import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.text.Normalizer;

@Log4j2
public final class StringUtils {


    private StringUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static @NotNull String toASCII(@NotNull String string) {
        try {
            var normalizedString = Normalizer.normalize(string, Normalizer.Form.NFD);
            return normalizedString
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                    .replaceAll("[^\\p{ASCII}]", "");
        } catch (Exception e) {
            throw new IllegalArgumentException("Error normalizando la cadena", e);
        }
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

}
