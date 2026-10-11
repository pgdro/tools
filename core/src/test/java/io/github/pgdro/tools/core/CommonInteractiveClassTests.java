package io.github.pgdro.tools.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Testing for CommonInteractiveClass
 */
@DisplayName("CommonInteractiveClass testing")
class CommonInteractiveClassTests {
    /** String for Original not equal to Expected */
    private static final String ORIG_NQ_EXPCT = "\"%s\" is not equal to \"%s\"";

    @Test
    @DisplayName("Folder destination option mixin get folder destination returns set value")
    void folderDestinationOptionMixinGetFolderDestinationReturnsSetValue() {
        final InteractiveInputClass.FolderDestinationOptionMixinClass mixin = new InteractiveInputClass.FolderDestinationOptionMixinClass();
        assertDoesNotThrow(mixin::getFolderDestination, "getFolderDestination should not throw");
    }

    /**
     * Test for ExitCode
     */
    @Nested
    /* default */ @DisplayName("ExitCode testing...")
    class TestExitCode {

        @Test
        @DisplayName("Set exit code stores the provided code")
        void setExitCodeStoresProvidedCode() {
            final int testCode = 42;
            InteractiveInputClass.setExitCode(testCode);
            final int testCodeStored = InteractiveInputClass.getExitCode();
            assertEquals(testCode, testCodeStored, String.format(ORIG_NQ_EXPCT, testCode, testCodeStored));
        }

        @Test
        @DisplayName("Set exit code with zero stores zero")
        void setExitCodeWithZeroStoresZero() {
            InteractiveInputClass.setExitCode(0);
            final int testCodeStored = InteractiveInputClass.getExitCode();
            assertEquals(0, testCodeStored, "Exit code zero should be valid");
        }

        @Test
        @DisplayName("Set exit code with negative value stores negative")
        void setExitCodeWithNegativeValueStoresNegative() {
            final int negativeCode = -1;
            InteractiveInputClass.setExitCode(negativeCode);
            final int testCodeStored = InteractiveInputClass.getExitCode();
            assertEquals(negativeCode, testCodeStored, "Negative exit code should be stored");
        }

        @Test
        @DisplayName("Set exit code overwrites previous value")
        void setExitCodeOverwritesPreviousValue() {
            InteractiveInputClass.setExitCode(10);
            InteractiveInputClass.setExitCode(20);
            final int testCodeStored = InteractiveInputClass.getExitCode();
            assertEquals(20, testCodeStored, "Exit code should be overwritten by latest value");
        }

        /**
         * Constructor
         */
        protected TestExitCode() {
            super();
        }

    }

    /**
     * Constructor
     */
    protected CommonInteractiveClassTests() {
        super();
    }
}
