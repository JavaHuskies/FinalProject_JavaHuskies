package util;

import service.AuthService;

/**
 * Centralized input validation utility for all panels.
 * <p>
 * All methods return a {@link ValidationResult} containing a pass/fail flag
 * and a user-facing message. Panels should call the relevant method before
 * processing any input and display {@code result.message} inline if
 * {@code result.valid} is {@code false}.
 * <p>
 * Password policy validation delegates to {@link AuthService} so the
 * policy is enforced from a single source of truth.
 */
public final class ValidationUtils {

    /** Regex for a valid email address. */
    private static final String emailPattern =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    /**
     * Regex for a valid username.
     * Allows letters, digits, underscores, and hyphens; 3–32 characters.
     */
    private static final String usernamePattern = "^[A-Za-z0-9_-]{3,32}$";

    /** Utility class — no instantiation. */
    private ValidationUtils() {}

    // ─────────────────────────────────────────────────────────────────────────
    // Result type
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Immutable result returned by every validation method.
     */
    public static final class ValidationResult {

        /** {@code true} if the value passed validation. */
        public final boolean valid;

        /** User-facing message; empty string when {@code valid} is {@code true}. */
        public final String message;

        /**
         * Constructs a ValidationResult.
         *
         * @param valid   whether the value passed validation
         * @param message user-facing message; empty string on success
         */
        private ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        /**
         * Returns a passing result with no message.
         *
         * @return a valid ValidationResult
         */
        static ValidationResult pass() {
            return new ValidationResult(true, "");
        }

        /**
         * Returns a failing result with the supplied message.
         *
         * @param message user-facing error description
         * @return an invalid ValidationResult
         */
        static ValidationResult fail(String message) {
            return new ValidationResult(false, message);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Validators
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Checks that a value is not null and not blank (empty or whitespace only).
     *
     * @param value     the input value to check
     * @param fieldName human-readable field label shown in the error message
     * @return a passing result if the value is non-blank; a failing result otherwise
     */
    public static ValidationResult requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return ValidationResult.fail(fieldName + " is required.");
        }
        return ValidationResult.pass();
    }

    /**
     * Checks that a numeric string represents a valid integer within an
     * inclusive range.
     *
     * @param value     the input value to check
     * @param fieldName human-readable field label shown in the error message
     * @param min       minimum acceptable integer value (inclusive)
     * @param max       maximum acceptable integer value (inclusive)
     * @return a passing result if the value is a valid integer in range;
     *         a failing result otherwise
     */
    public static ValidationResult requireInteger(String value, String fieldName,
                                                   int min, int max) {
        if (value == null || value.isBlank()) {
            return ValidationResult.fail(fieldName + " is required.");
        }
        try {
            int parsed = Integer.parseInt(value.trim());
            if (parsed < min || parsed > max) {
                return ValidationResult.fail(
                        fieldName + " must be between " + min + " and " + max + ".");
            }
        } catch (NumberFormatException e) {
            return ValidationResult.fail(fieldName + " must be a whole number.");
        }
        return ValidationResult.pass();
    }

    /**
     * Checks that a value matches a basic email format.
     *
     * @param value the input value to check
     * @return a passing result if the value is a valid email address;
     *         a failing result otherwise
     */
    public static ValidationResult requireEmail(String value) {
        ValidationResult blank = requireNonBlank(value, "Email");
        if (!blank.valid) return blank;
        if (!value.trim().matches(emailPattern)) {
            return ValidationResult.fail("Email address is not valid.");
        }
        return ValidationResult.pass();
    }

    /**
     * Checks that a value matches the allowed username format:
     * letters, digits, underscores, and hyphens; 3–32 characters.
     *
     * @param value the input value to check
     * @return a passing result if the value is a valid username;
     *         a failing result otherwise
     */
    public static ValidationResult requireUsername(String value) {
        ValidationResult blank = requireNonBlank(value, "Username");
        if (!blank.valid) return blank;
        if (!value.trim().matches(usernamePattern)) {
            return ValidationResult.fail(
                    "Username must be 3–32 characters and contain only " +
                    "letters, digits, underscores, or hyphens.");
        }
        return ValidationResult.pass();
    }

    /**
     * Delegates password policy validation to {@link AuthService}.
     * <p>
     * Calls {@link AuthService#getPasswordPolicyMessage(String)} — if a
     * non-null message is returned the password violates policy and the
     * message is surfaced as the failing result. A null return means all
     * rules are satisfied.
     *
     * @param value the plain-text password candidate to check
     * @return a passing result if the password meets policy;
     *         a failing result with the policy message otherwise
     */
    public static ValidationResult requireValidPassword(String value) {
        ValidationResult blank = requireNonBlank(value, "Password");
        if (!blank.valid) return blank;
        AuthService auth = AuthService.getInstance();
        String policyError = auth.getPasswordPolicyMessage(value);
        if (policyError != null) {
            return ValidationResult.fail(policyError);
        }
        return ValidationResult.pass();
    }
}