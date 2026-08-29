package ru.drmemex.classifieds.common.util.string;

import java.util.Locale;

public final class StringNormalizer {

    private StringNormalizer() {
        throw new AssertionError("Utility class");
    }

    public static String normalizeDisplayName(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim()
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);

        StringBuilder result = new StringBuilder(normalized.length());

        boolean capitalizeNext = true;

        for (char current : normalized.toCharArray()) {

            if (Character.isWhitespace(current)) {
                capitalizeNext = true;
                result.append(current);
                continue;
            }

            if (capitalizeNext) {
                result.append(Character.toUpperCase(current));
                capitalizeNext = false;
            } else {
                result.append(current);
            }
        }

        return result.toString();
    }

    public static String normalizeComparable(String value) {
        if (value == null) {
            return null;
        }

        return value.trim()
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }
}