package com.redhat.vcs.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class UtilsTest {

    @Test
    public void emailTest() {
        assertTrue( Utils.isEmailBlankOrValid(" "));
        assertTrue( Utils.isEmailBlankOrValid(null));
        assertTrue( Utils.isEmailBlankOrValid("alternateEmail@redhat.com"));
        assertTrue( Utils.isEmailValid("alternateEmail@redhat.com"));
        assertFalse( Utils.isEmailValid(null));
        assertFalse( Utils.isEmailValid("alternateEmailredhat.com"));

        char[] charArray = new char[Utils.MAX_EMAIL_LENGTH];
        Arrays.fill(charArray, 'a');
        String longEmailAddress = new String(charArray) + "@redhat.com";
        assertFalse( Utils.isEmailValid(longEmailAddress));
    }

    @Test
    public void maskedStringDefaultTest() {
        for (int i=0 ; i<1000 ; i++) {
        String maskedString = Utils.maskedString();
            assertTrue(maskedString.length() >= Utils.DEFAULT_MIN_LENGTH);
            assertTrue(maskedString.length() <= Utils.DEFAULT_MAX_LENGTH);
            assertTrue(maskedString.contains(new String(new char[Utils.DEFAULT_MIN_LENGTH]).replace('\0', Utils.DEFAULT_MASK_CHARACTER)));
        }
    }
    
    @Test
    public void maskedStringExactTest() {
        String maskedString = Utils.maskedString(7,7);
        assertTrue( maskedString.equals(new String(new char[7]).replace('\0', Utils.DEFAULT_MASK_CHARACTER)));
    }

    @Test
    public void maskedStringRangeTest() {
        String maskedString = Utils.maskedString(2,12);
        assertTrue(maskedString.length() >= 2 );
        assertTrue(maskedString.length() <= 12 );
        assertTrue(maskedString.contains(new String(new char[2]).replace('\0', Utils.DEFAULT_MASK_CHARACTER)));
    }

    @Test
    public void maskedStringCustomTest() {
        String maskedString = Utils.maskedString(8,12,'c');
        assertTrue(maskedString.length() >= 8 );
        assertTrue(maskedString.length() <= 12 );
        assertTrue(maskedString.contains(new String(new char[8]).replace('\0', 'c')));
    }

    @Test
    public void maskedDobTest (){
        assertEquals(Utils.maskedDOB(), Utils.MASKED_DOB_STRING);
    }

}
