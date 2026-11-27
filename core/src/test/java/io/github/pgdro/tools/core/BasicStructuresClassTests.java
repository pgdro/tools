package io.github.pgdro.tools.core;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SequencedMap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Testing for BasicStructuresClass
 */
@DisplayName("BasicStructuresClass testing")
class BasicStructuresClassTests {
    /** String for Original not equal to Expected */
    private static final String ORIG_NQ_EXPCT = "\"%s\" is not equal to \"%s\"";
    /** Constant for first */
    /* default */ private static final String STR_FIRST = "first";

    /** Constructor */
    BasicStructuresClassTests() {
        super();
    }

    @Test
    @DisplayName("Simple test to verify that 51 is same as 51 divided by 100")
    void testComputePercentageSafelySimple() {
        final BigDecimal original = new BigDecimal(51).setScale(2, RoundingMode.HALF_UP);
        final BigDecimal handled = BasicStructuresClass.computePercentageSafely(51, 100);
        assertEquals(original, handled, String.format(ORIG_NQ_EXPCT, handled, original));
    }

    @Test
    @DisplayName("Simple test to verify that 0 is same as 51 divided by 0")
    void testComputePercentageSafelyZeroDivision() {
        final BigDecimal original = new BigDecimal(55).setScale(2, RoundingMode.HALF_UP);
        final BigDecimal handled = BasicStructuresClass.computePercentageSafely(55, 0);
        assertEquals(original, handled, String.format(ORIG_NQ_EXPCT, handled, original));
    }

    @Test
    @DisplayName("Simple test to verify that 51.123 is not the same as 51.123 divided by 1000")
    void testComputePercentageSafelyNotEnoughPrecision() {
        final BigDecimal expected = new BigDecimal("51.123");
        final BigDecimal handled = BasicStructuresClass.computePercentageSafely(51_123L, 100_000L);
        assertNotEquals(expected, handled, String.format(ORIG_NQ_EXPCT, handled, expected));
    }

    @Test
    @DisplayName("Simple test to verify that 2026 is same as converted value from String 2026")
    void testConvertStringIntoBigDecimal() {
        final BigDecimal expected = new BigDecimal("2026");
        final BigDecimal handled = BasicStructuresClass.convertStringIntoBigDecimal("2026");
        assertEquals(expected, handled, String.format(ORIG_NQ_EXPCT, handled, expected));
    }

    @Test
    @DisplayName("Simple test to verify that 2026 is not the same as converted value from String 01.01.2026")
    void testConvertStringIntoBigDecimalInvalid() {
        final String strOriginal = "01.01.2026";
        final BigDecimal handled = BasicStructuresClass.convertStringIntoBigDecimal(strOriginal);
        assertNull(handled, String.format("Given String %s cannot be converted into BigDecimal due to incompatibility", strOriginal));
    }

    @Test
    @DisplayName("Simple test to verify that 20 is same as Integer 20")
    void testConvertStringIntoInteger() {
        final int expected = 20;
        final int handled = BasicStructuresClass.convertStringIntoInteger("20");
        assertEquals(expected, handled, String.format(ORIG_NQ_EXPCT, handled, expected));
    }

    @Test
    @DisplayName("Simple test to verify that 2026 is not the same as converted value from String 01.01.2026")
    void testConvertStringIntoIntegerInvalid() {
        assertNotEquals(2_026, BasicStructuresClass.convertStringIntoInteger("01.01.2026"));
    }

    /**
     * Test for StringEvaluationClass
     */
    @Nested
    /* default */ @DisplayName("ListAndMapSubClass testing...")
    final class TestListAndMapSubClass {

        /**
         * Constructor
         */
        private TestListAndMapSubClass() {
            super();
        }

        @Test
        @DisplayName("assertAll: Get word counts from list with space separator returns correct counts")
        void testGetWordCountsWithSpaceSeparator() {
            final List<String> valList = new ArrayList<>();
            valList.add("pear banana pear");
            valList.add("banana cherry pear");
            final SequencedMap<String, Long> wordCounts = BasicStructuresClass.ListAndMapSubClass.getWordCounts(valList, " ");
            assertAll("Get word counts from list with space separator returns correct counts",
                    () -> assertEquals(3, wordCounts.get("pear"), String.format(ORIG_NQ_EXPCT, wordCounts.get("pear"), 3)),
                    () -> assertEquals(2, wordCounts.get("banana"), String.format(ORIG_NQ_EXPCT, wordCounts.get("banana"), 2)),
                    () -> assertEquals(1, wordCounts.get("cherry"), String.format(ORIG_NQ_EXPCT, wordCounts.get("cherry"), 1))
            );
        }

        @Test
        @DisplayName("Get word counts returns empty map for empty list")
        void testGetWordCountsWithEmptyListReturnsEmpty() {
            final List<String> valList = new ArrayList<>();
            final SequencedMap<String, Long> wordCounts = BasicStructuresClass.ListAndMapSubClass.getWordCounts(valList, " ");
            assertTrue(wordCounts.isEmpty(), "Word counts should be empty for empty input list");
        }

        @Test
        @DisplayName("Get word counts orders words by frequency descending")
        void testGetWordCountsOrdersByFrequencyDescending() {
            final List<String> valList = new ArrayList<>();
            valList.add("a a a b b c");
            final SequencedMap<String, Long> wordCounts = BasicStructuresClass.ListAndMapSubClass.getWordCounts(valList, " ");
            final List<String> keys = new ArrayList<>(wordCounts.keySet());
            assertAll("Get word counts orders words by frequency descending",
                    () -> assertEquals("a", keys.getFirst(), "Most frequent word should be first"),
                    () -> assertEquals("b", keys.get(1), "Second frequent word should be second"),
                    () -> assertEquals("c", keys.get(2), "Least frequent word should be last")
            );
        }

        @SuppressWarnings("null")
        @Test
        @DisplayName("Convert map of strings into list of properties creates correct structure")
        void convertMapOfStringsIntoListOfPropertiesCreatesCorrectStructure() {
            final Map<String, Object> inMap = Map.of(
                    "key11", "value11",
                    "key2", "value2");
            final List<Properties> result = BasicStructuresClass.ListAndMapSubClass.convertMapOfStringsIntoListOfProperties("TestCategory", inMap);
            assertAll("Convert map of strings into list of properties creates correct structure",
                    () -> assertEquals(2, result.size(), "Result should have 2 property objects"),
                    () -> assertEquals("TestCategory", result.getFirst().getProperty(ConfigurationClass.STR_CATEGORY), "Category should be set")
            );
        }

        @SuppressWarnings("null")
        @Test
        @DisplayName("Convert map of strings into list of properties sorts by element name")
        void convertMapOfStringsIntoListOfPropertiesSortsByElementName() {
            final Map<String, Object> inMap = Map.of(
                    "zebra", "value1",
                    "apple", "value2");
            final List<Properties> result = BasicStructuresClass.ListAndMapSubClass.convertMapOfStringsIntoListOfProperties("Cat", inMap);
            assertEquals("apple", result.getFirst().getProperty("Element"), "Should be sorted alphabetically");
        }

        @SuppressWarnings("null")
        @Test
        @DisplayName("Merge keys preserves non-merged keys")
        void mergeKeysPreservesNonMergedKeys() {
            final Map<String, List<String>> inputMap = Map.of(
                    "a", new ArrayList<>(List.of("v1")),
                    "b", new ArrayList<>(List.of("v2")));
            final Map<List<String>, String> mergeRules = Map.of(
                    List.of("a"), "merged");
            final Map<String, List<String>> result = BasicStructuresClass.ListAndMapSubClass.mergeKeys(inputMap, mergeRules);
            assertTrue(result.containsKey("b"), "Non-merged key should be preserved");
        }

        @Test
        @DisplayName("Sort properties maintains order specified in list")
        void sortPropertiesMaintainsOrderSpecifiedInList() {
            final Properties prop = new Properties();
            prop.put("third", "value3");
            prop.put(STR_FIRST, "value1");
            prop.put("second", "value2");
            final List<String> order = Arrays.asList(STR_FIRST, "second", "third");
            final SequencedMap<Object, Object> result = BasicStructuresClass.ListAndMapSubClass.sortProperties(prop, order);
            final List<Object> keys = new ArrayList<>(result.keySet());
            assertAll("Sort properties maintains order specified in list",
                    () -> assertEquals(STR_FIRST, keys.getFirst(), "First key should be ordered first"),
                    () -> assertEquals("second", keys.get(1), "Second key should be ordered second")
            );
        }

        @Test
        @DisplayName("Sort properties puts unspecified keys at end")
        void sortPropertiesPutsUnspecifiedKeysAtEnd() {
            final Properties prop = new Properties();
            prop.put("unspecified", "value");
            prop.put(STR_FIRST, "value1");
            final List<String> order = List.of(STR_FIRST);
            final SequencedMap<Object, Object> result = BasicStructuresClass.ListAndMapSubClass.sortProperties(prop, order);
            final List<Object> keys = new ArrayList<>(result.keySet());
            assertAll("Sort properties puts unspecified keys at end",
                    () -> assertEquals(STR_FIRST, keys.getFirst(), "Specified key should come first"),
                    () -> assertEquals("unspecified", keys.get(1), "Unspecified key should come last")
            );
        }

    }

}
