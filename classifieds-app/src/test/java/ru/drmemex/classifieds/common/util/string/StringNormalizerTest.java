package ru.drmemex.classifieds.common.util.string;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StringNormalizerTest {

    @Test
    void normalizeDisplayName_ShouldNormalizeValue() {

        String result =
                StringNormalizer.normalizeDisplayName(
                        "  эЛЕКТРОНИКА   бытовая  "
                );

        assertEquals(
                "Электроника Бытовая",
                result
        );
    }

    @Test
    void normalizeDisplayName_ShouldReturnNull_WhenValueIsNull() {

        String result =
                StringNormalizer.normalizeDisplayName(null);

        assertNull(result);
    }

    @Test
    void normalizeComparable_ShouldNormalizeValue() {

        String result =
                StringNormalizer.normalizeComparable(
                        "  ЭЛЕКТРОНИКА   Бытовая  "
                );

        assertEquals(
                "электроника бытовая",
                result
        );
    }

    @Test
    void normalizeComparable_ShouldReturnNull_WhenValueIsNull() {

        String result =
                StringNormalizer.normalizeComparable(null);

        assertNull(result);
    }
}