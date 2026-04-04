package util;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for ValidationUtils.
 * Covers all validators: requireNonBlank, requireInteger, requireEmail,
 * requireUsername, and requireValidPassword.
 *
 * @author John Baldwin
 */
public class ValidationUtilsTest {

    // ── requireNonBlank ───────────────────────────────────────────────────────

    @Test
    public void testRequireNonBlankPassesForValidValue() {
        assertTrue("Non-blank value should pass",
                ValidationUtils.requireNonBlank("hello", "Field").valid);
    }

    @Test
    public void testRequireNonBlankFailsForNull() {
        assertFalse("Null value should fail",
                ValidationUtils.requireNonBlank(null, "Field").valid);
    }

    @Test
    public void testRequireNonBlankFailsForEmptyString() {
        assertFalse("Empty string should fail",
                ValidationUtils.requireNonBlank("", "Field").valid);
    }

    @Test
    public void testRequireNonBlankFailsForWhitespaceOnly() {
        assertFalse("Whitespace-only string should fail",
                ValidationUtils.requireNonBlank("   ", "Field").valid);
    }

    @Test
    public void testRequireNonBlankMessageContainsFieldName() {
        String msg = ValidationUtils.requireNonBlank(null, "Username").message;
        assertTrue("Error message should contain field name",
                msg.contains("Username"));
    }

    @Test
    public void testRequireNonBlankPassReturnsEmptyMessage() {
        String msg = ValidationUtils.requireNonBlank("value", "Field").message;
        assertEquals("Passing result should have empty message", "", msg);
    }

    // ── requireInteger ────────────────────────────────────────────────────────

    @Test
    public void testRequireIntegerPassesForValueInRange() {
        assertTrue("Value in range should pass",
                ValidationUtils.requireInteger("5", "Age", 1, 10).valid);
    }

    @Test
    public void testRequireIntegerPassesForMinBoundary() {
        assertTrue("Min boundary value should pass",
                ValidationUtils.requireInteger("1", "Age", 1, 10).valid);
    }

    @Test
    public void testRequireIntegerPassesForMaxBoundary() {
        assertTrue("Max boundary value should pass",
                ValidationUtils.requireInteger("10", "Age", 1, 10).valid);
    }

    @Test
    public void testRequireIntegerFailsForValueBelowMin() {
        assertFalse("Value below min should fail",
                ValidationUtils.requireInteger("0", "Age", 1, 10).valid);
    }

    @Test
    public void testRequireIntegerFailsForValueAboveMax() {
        assertFalse("Value above max should fail",
                ValidationUtils.requireInteger("11", "Age", 1, 10).valid);
    }

    @Test
    public void testRequireIntegerFailsForNonNumericString() {
        assertFalse("Non-numeric string should fail",
                ValidationUtils.requireInteger("abc", "Age", 1, 10).valid);
    }

    @Test
    public void testRequireIntegerFailsForNull() {
        assertFalse("Null value should fail",
                ValidationUtils.requireInteger(null, "Age", 1, 10).valid);
    }

    @Test
    public void testRequireIntegerFailsForBlank() {
        assertFalse("Blank value should fail",
                ValidationUtils.requireInteger("", "Age", 1, 10).valid);
    }

    @Test
    public void testRequireIntegerMessageContainsFieldName() {
        String msg = ValidationUtils.requireInteger("abc", "Age", 1, 10).message;
        assertTrue("Error message should contain field name",
                msg.contains("Age"));
    }

    // ── requireEmail ──────────────────────────────────────────────────────────

    @Test
    public void testRequireEmailPassesForValidEmail() {
        assertTrue("Valid email should pass",
                ValidationUtils.requireEmail("zaphod@heartofgold.com").valid);
    }

    @Test
    public void testRequireEmailPassesForSubdomain() {
        assertTrue("Subdomain email should pass",
                ValidationUtils.requireEmail("user@mail.deepthought.com").valid);
    }

    @Test
    public void testRequireEmailPassesForPlusAddress() {
        assertTrue("Plus-addressed email should pass",
                ValidationUtils.requireEmail("user+tag@example.com").valid);
    }

    @Test
    public void testRequireEmailFailsForMissingAtSign() {
        assertFalse("Email without @ should fail",
                ValidationUtils.requireEmail("zaphodheartofgold.com").valid);
    }

    @Test
    public void testRequireEmailFailsForMissingDomain() {
        assertFalse("Email without domain should fail",
                ValidationUtils.requireEmail("zaphod@").valid);
    }

    @Test
    public void testRequireEmailFailsForMissingTld() {
        assertFalse("Email without TLD should fail",
                ValidationUtils.requireEmail("zaphod@heartofgold").valid);
    }

    @Test
    public void testRequireEmailFailsForNull() {
        assertFalse("Null email should fail",
                ValidationUtils.requireEmail(null).valid);
    }

    @Test
    public void testRequireEmailFailsForBlank() {
        assertFalse("Blank email should fail",
                ValidationUtils.requireEmail("").valid);
    }

    @Test
    public void testRequireEmailFailsForSpacesInAddress() {
        assertFalse("Email with spaces should fail",
                ValidationUtils.requireEmail("zaphod @heartofgold.com").valid);
    }

    // ── requireUsername ───────────────────────────────────────────────────────

    @Test
    public void testRequireUsernamePassesForValidUsername() {
        assertTrue("Valid username should pass",
                ValidationUtils.requireUsername("zaphod42").valid);
    }

    @Test
    public void testRequireUsernamePassesWithUnderscore() {
        assertTrue("Username with underscore should pass",
                ValidationUtils.requireUsername("zaphod_42").valid);
    }

    @Test
    public void testRequireUsernamePassesWithHyphen() {
        assertTrue("Username with hyphen should pass",
                ValidationUtils.requireUsername("zaphod-42").valid);
    }

    @Test
    public void testRequireUsernamePassesAtMinLength() {
        assertTrue("3-character username should pass",
                ValidationUtils.requireUsername("abc").valid);
    }

    @Test
    public void testRequireUsernamePassesAtMaxLength() {
        assertTrue("32-character username should pass",
                ValidationUtils.requireUsername("a".repeat(32)).valid);
    }

    @Test
    public void testRequireUsernameFailsTooShort() {
        assertFalse("2-character username should fail",
                ValidationUtils.requireUsername("ab").valid);
    }

    @Test
    public void testRequireUsernameFailsTooLong() {
        assertFalse("33-character username should fail",
                ValidationUtils.requireUsername("a".repeat(33)).valid);
    }

    @Test
    public void testRequireUsernameFailsForSpaces() {
        assertFalse("Username with spaces should fail",
                ValidationUtils.requireUsername("zaphod beeblebrox").valid);
    }

    @Test
    public void testRequireUsernameFailsForSpecialChars() {
        assertFalse("Username with @ should fail",
                ValidationUtils.requireUsername("zaphod@42").valid);
    }

    @Test
    public void testRequireUsernameFailsForNull() {
        assertFalse("Null username should fail",
                ValidationUtils.requireUsername(null).valid);
    }

    @Test
    public void testRequireUsernameFailsForBlank() {
        assertFalse("Blank username should fail",
                ValidationUtils.requireUsername("").valid);
    }

    // ── requireValidPassword ──────────────────────────────────────────────────

    @Test
    public void testRequireValidPasswordPassesForStrongPassword() {
        assertTrue("Strong password should pass",
                ValidationUtils.requireValidPassword("Admin1!").valid);
    }

    @Test
    public void testRequireValidPasswordFailsForNoUppercase() {
        assertFalse("Password without uppercase should fail",
                ValidationUtils.requireValidPassword("admin1!").valid);
    }

    @Test
    public void testRequireValidPasswordFailsForNoLowercase() {
        assertFalse("Password without lowercase should fail",
                ValidationUtils.requireValidPassword("ADMIN1!").valid);
    }

    @Test
    public void testRequireValidPasswordFailsForNoDigit() {
        assertFalse("Password without digit should fail",
                ValidationUtils.requireValidPassword("AdminOne!").valid);
    }

    @Test
    public void testRequireValidPasswordFailsForNoSpecialChar() {
        assertFalse("Password without special character should fail",
                ValidationUtils.requireValidPassword("Admin123").valid);
    }

    @Test
    public void testRequireValidPasswordFailsForTooShort() {
        assertFalse("Password under 8 characters should fail",
                ValidationUtils.requireValidPassword("Ad1!").valid);
    }

    @Test
    public void testRequireValidPasswordFailsForNull() {
        assertFalse("Null password should fail",
                ValidationUtils.requireValidPassword(null).valid);
    }

    @Test
    public void testRequireValidPasswordFailsForBlank() {
        assertFalse("Blank password should fail",
                ValidationUtils.requireValidPassword("").valid);
    }

    @Test
    public void testRequireValidPasswordMessageNotEmptyOnFailure() {
        String msg = ValidationUtils.requireValidPassword("weak").message;
        assertNotNull("Failing result should have a message", msg);
        assertFalse("Failing result message should not be empty", msg.isEmpty());
    }

    @Test
    public void testRequireValidPasswordMessageEmptyOnPass() {
        String msg = ValidationUtils.requireValidPassword("Admin1!").message;
        assertEquals("Passing result should have empty message", "", msg);
    }
}