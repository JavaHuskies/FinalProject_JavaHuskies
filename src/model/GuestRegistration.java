package model;

/**
 * Form object carrying guest self-registration data from
 * {@code GuestRegistrationPanel} to {@link service.AuthService#registerGuest}.
 *
 * <p>STUB — may be replaced or absorbed into Anan's model classes when
 * PersistenceService is delivered. Fabio's GuestRegistrationPanel depends
 * on this interface — coordinate before removing.
 * presence checks. Password policy enforcement is handled separately by
 * {@link service.AuthService#enforcePasswordPolicy} after this object is
 * validated.
 *
 * <p>Fabio's GuestRegistrationPanel constructs this object from form fields
 * and passes it to AuthService — it does not perform hashing or persistence
 * directly.
 *
 * Usage:
 * <pre>
 *   GuestRegistration form = new GuestRegistration(
 *       firstName, lastName, email, password, confirmPassword);
 *   Guest guest = AuthService.getInstance().registerGuest(form);
 * </pre>
 */
public class GuestRegistration {

    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;
    private final String confirmPassword;

    /**
     * Constructs a guest registration form object.
     *
     * @param firstName       guest's first name
     * @param lastName        guest's last name
     * @param email           guest's email address
     * @param password        chosen password (plaintext — hashed by AuthService)
     * @param confirmPassword password confirmation field — must match password
     */
    public GuestRegistration(String firstName, String lastName, String email,
                              String password, String confirmPassword) {
        this.firstName       = firstName;
        this.lastName        = lastName;
        this.email           = email;
        this.password        = password;
        this.confirmPassword = confirmPassword;
    }

    // ── Validation ────────────────────────────────────────────────────────────

    /**
     * Validates required fields and confirms passwords match.
     * Does not enforce password policy — that is handled by
     * {@link service.AuthService#getPasswordPolicyMessage}.
     *
     * @return an error message string if validation fails, or null if all
     *         fields are present and passwords match
     */
    public String validate() {
        if (firstName == null || firstName.isBlank())
            return "First name is required.";
        if (lastName == null || lastName.isBlank())
            return "Last name is required.";
        if (email == null || email.isBlank())
            return "Email address is required.";
        if (!email.contains("@") || !email.contains("."))
            return "Email address is not valid.";
        if (password == null || password.isBlank())
            return "Password is required.";
        if (!password.equals(confirmPassword))
            return "Passwords do not match.";
        return null; // null = validation passed
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    /** @return guest's first name */
    public String getFirstName()       { return firstName; }

    /** @return guest's last name */
    public String getLastName()        { return lastName; }

    /** @return guest's email address */
    public String getEmail()           { return email; }

    /** @return plaintext password — only read by AuthService for hashing */
    public String getPassword()        { return password; }

    /** @return password confirmation value */
    public String getConfirmPassword() { return confirmPassword; }
}