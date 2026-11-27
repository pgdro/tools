package io.github.pgdro.tools.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Testing for BasicStructuresClass
 */
@DisplayName("StringingClass testing")
public class StringingClassTest {
    /** String for Original not equal to Expected */
    private static final String ORIG_NQ_EXPCT = "\"%s\" is not equal to \"%s\"";
    /** Constant for first */
    /* default */ private static final String STR_FIRST = "first";

    @Test
    @DisplayName("Counting 3 named parameters within a dummy query")
    void testCountNamedParametersWithinQuery() {
        final String strHaystack = "SELECT {Field1} FROM table WHERE {Field 2} = {Value_to_filter};";
        final int intOriginal = 3;
        final int handled = StringingClass.countNamedParametersWithinQuery(strHaystack);
        assertEquals(intOriginal, handled, String.format(ORIG_NQ_EXPCT, handled, intOriginal));
    }

    @Test
    @DisplayName("Counting 3 positional type parameters within a dummy query")
    void testCountPositionalTypeParametersWithinQuery() {
        final String strHaystack = "SELECT %s FROM table WHERE %s = %d;";
        final int intOriginal = 3;
        final int handled = StringingClass.countPositionalTypeParametersWithinQuery(strHaystack);
        assertEquals(intOriginal, handled, String.format(ORIG_NQ_EXPCT, handled, intOriginal));
    }

    /**
     * Test for StringCleaningClass
     */
    @Nested
    /* default */ @DisplayName("StringCleaningClass testing...")
    final class TestStringCleaningSubClass {

        /**
         * Constructor
         */
        private TestStringCleaningSubClass() {
            super();
        }

        @Test
        void testCleanStringAsDatabaseObject() {
            final String strOriginal = "Original1";
            final String handled = StringingClass.StringCleaningSubClass.cleanStringAsDatabaseObject(strOriginal + "^");
            assertEquals(strOriginal, handled, String.format(ORIG_NQ_EXPCT, handled, strOriginal));
        }

        @Test
        void testCleanStringFromCurlyBraces() {
            final String strOriginal = "Original2";
            final String handled = StringingClass.StringCleaningSubClass.cleanStringFromCurlyBraces("{" + strOriginal + "}");
            assertEquals(strOriginal, handled, String.format(ORIG_NQ_EXPCT, handled, strOriginal));
        }

        @Test
        void testCleanStringFromUnwantedCharacters() {
            final String strOriginal = "Original3";
            final String handled = StringingClass.StringCleaningSubClass.cleanStringFromUnwantedCharacters(strOriginal + "^");
            assertEquals(strOriginal, handled, String.format(ORIG_NQ_EXPCT, handled, strOriginal));
        }

        @Test
        void testStripQuotes() {
            final String strOriginal = "Original4";
            final String handled = StringingClass.StringCleaningSubClass.stripQuotes("\"" + strOriginal + "\"");
            assertEquals(strOriginal, handled, String.format(ORIG_NQ_EXPCT, handled, strOriginal));
        }

        @Test
        void testStripQuotesShort() {
            final String strOriginal = "O";
            final String handled = StringingClass.StringCleaningSubClass.stripQuotes("\"");
            assertNotEquals(strOriginal, handled, String.format(ORIG_NQ_EXPCT, handled, strOriginal));
        }

    }

    /**
     * Test for StringConversionClass
     */
    @Nested
    /* default */ @DisplayName("StringConversionClass testing...")
    final class TestStringConversionSubClass {

        /**
         * Constructor
         */
        private TestStringConversionSubClass() {
            super();
        }

        @Test
        void testConvertPromptParametersIntoNamedParameters() {
            final String strOriginal = "SELECT {Field A}";
            final String strExpected = "SELECT :Field_A";
            final String handled = StringingClass.StringConversionSubClass.convertPromptParametersIntoNamedParameters(strOriginal);
            assertEquals(strExpected, handled, String.format(ORIG_NQ_EXPCT, handled, strExpected));
        }

        @Test
        void testConvertPromptParametersIntoParameters() {
            final String strOriginal = "SELECT {Field A}";
            final String strExpected = "SELECT ?";
            final String handled = StringingClass.StringConversionSubClass.convertPromptParametersIntoParameters(strOriginal);
            assertEquals(strExpected, handled, String.format(ORIG_NQ_EXPCT, handled, strExpected));
        }

    }

    /**
     * Test for StringEvaluationClass
     */
    @Nested
    /* default */ @DisplayName("TestStringEvaluationClass testing...")
    final class TestStringEvaluationSubClass {

        /**
         * Constructor
         */
        private TestStringEvaluationSubClass() {
            super();
        }

        @Test
        void testHasMatchingSubstring() {
            final List<String> listStrings = new ArrayList<>();
            listStrings.add(STR_FIRST);
            listStrings.add("Second");
            final boolean handled = StringingClass.StringEvaluationSubClass.hasMatchingSubstring(STR_FIRST, listStrings);
            assertTrue(handled, String.format("\"%s\" is not true as expected", handled));
        }

    }

    /**
     * Test for StringEvaluationClass
     */
    @Nested
    /* default */ @DisplayName("StringTransformationClass testing...")
    final class TestStringTransformationClass {

        /**
         * Constructor
         */
        private TestStringTransformationClass() {
            super();
        }

        @Test
        @DisplayName("Compute string signature produces consistent output")
        void computeStringSignatureProducesConsistentOutput() {
            final String input = "test_input";
            final String first = StringingClass.StringTransformationSubClass.computeStringSignature(input);
            final String second = StringingClass.StringTransformationSubClass.computeStringSignature(input);
            assertEquals(first, second, "Same input should produce same signature");
        }

        @Test
        @DisplayName("Compute string signature produces different output for different inputs")
        void computeStringSignatureProducesDifferentOutputForDifferentInputs() {
            final String sig1 = StringingClass.StringTransformationSubClass.computeStringSignature("input1");
            final String sig2 = StringingClass.StringTransformationSubClass.computeStringSignature("input2");
            assertNotEquals(sig1, sig2, "Different inputs should produce different signatures");
        }

        @Test
        @DisplayName("Compute string signature produces non-empty output")
        void computeStringSignatureProducesNonEmptyOutput() {
            final String signature = StringingClass.StringTransformationSubClass.computeStringSignature("test");
            assertFalse(signature.isEmpty(), "Signature should not be empty");
        }

        @Test
        @DisplayName("Enclose string if contains space adds quotes when space present")
        void encloseStringIfContainsSpaceAddsQuotesWhenSpacePresent() {
            final String strOriginal = "\"hello world\"";
            final String strExpected = "\"hello world\"";
            final String result = StringingClass.StringTransformationSubClass.encloseStringIfContainsSpace(strOriginal, '"');
            assertEquals(strExpected, result, String.format(ORIG_NQ_EXPCT, result, strExpected));
        }

        @Test
        @DisplayName("Enclose string if contains space leaves unchanged when no space")
        void encloseStringIfContainsSpaceLeaveUnchangedWhenNoSpace() {
            final String strOriginal = "helloworld";
            final String strExpected = "helloworld";
            final String handled = StringingClass.StringTransformationSubClass.encloseStringIfContainsSpace(strOriginal, '\"');
            assertEquals(strExpected, handled, String.format(ORIG_NQ_EXPCT, handled, strExpected));
        }

        @Test
        void testEncloseStringIfContainsSpace() {
            final String strOriginal = "Original String";
            final String strExpected = "\"Original String\"";
            final String handled = StringingClass.StringTransformationSubClass.encloseStringIfContainsSpace(strOriginal, '\"');
            assertEquals(strExpected, handled, String.format(ORIG_NQ_EXPCT, handled, strExpected));
        }

        @Test
        void testEncloseStringIfContainsSpaceFutile() {
            final String strExpected = "\"Original String\"";
            final String handled = StringingClass.StringTransformationSubClass.encloseStringIfContainsSpace(strExpected, '\"');
            assertEquals(strExpected, handled, String.format(ORIG_NQ_EXPCT, handled, strExpected));
        }

        @Test
        void testEncloseStringIfContainsSpacePartialEnd() {
            final String strOriginal = "\"Original String";
            final String strExpected = strOriginal + '"';
            final String handled = StringingClass.StringTransformationSubClass.encloseStringIfContainsSpace(strOriginal, '\"');
            assertEquals(strExpected, handled, String.format(ORIG_NQ_EXPCT, handled, strExpected));
        }

        @Test
        void testEncloseStringIfContainsSpacePartialStart() {
            final String strOriginal = "Original String\"";
            final String strExpected = '"' + strOriginal;
            final String handled = StringingClass.StringTransformationSubClass.encloseStringIfContainsSpace(strOriginal, '\"');
            assertEquals(strExpected, handled, String.format(ORIG_NQ_EXPCT, handled, strExpected));
        }

        @Test
        void testObfuscateProperties() {
            final Properties originalProps = new Properties();
            originalProps.put("key", "value");
            originalProps.put("password", "password");
            final Properties expectedProps = new Properties();
            expectedProps.put("key", "value");
            expectedProps.put("password", "*U*N*D*I*S*C*L*O*S*E*D*");
            final Properties handled = StringingClass.StringTransformationSubClass.obfuscateProperties(originalProps);
            assertEquals(expectedProps, handled, String.format(ORIG_NQ_EXPCT, handled, expectedProps));
        }

    }

}
