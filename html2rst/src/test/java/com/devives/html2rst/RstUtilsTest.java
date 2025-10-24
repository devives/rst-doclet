package com.devives.html2rst;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RstUtilsTest {

    @Test
    public void escapeEndingUnderlines() {
        Assertions.assertEquals("_fo_ob_ar\\_", RstUtils.escapeEndingUnderlines("_fo_ob_ar_"));
    }

    @Test
    public void escapeRstEmphasis() {
        Assertions.assertEquals("some\\\\bar", RstUtils.escapeRstEmphasis("some\\bar"));
        Assertions.assertEquals("some\\\\*bar\\\\*", RstUtils.escapeRstEmphasis("some\\*bar\\*"));
    }

}
