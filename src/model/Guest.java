package model;

/**
 * Represents a registered guest user.
 * Created by {@link service.AuthService#registerGuest} and persisted via
 * PersistenceService (pending). A guest cannot log in until
 * {@code emailVerified} is set to {@code true} via
 * {@link service.AuthService#confirmVerification}.
 *
 * <p>STUB — Anan expands this class when delivering PersistenceService model classes.
 * Fields map to the GUEST table in schema.sql.
 * Setters for mutable fields (emailVerified) are provided for PersistenceService
 * to update the record after verification.
 */
public class Guest {

    private final String guestId;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String passwordHash;
    private final String verificationToken;
    private boolean emailVerified;

    /**
     * Constructs a new Guest record.
     *
     * @param guestId           UUID primary key
     * @param firstName         guest's first name
     * @param lastName          guest's last name
     * @param email             guest's email address (used as login username)
     * @param passwordHash      BCrypt hash of the guest's password
     * @param verificationToken UUID token sent in the verification email
     * @param emailVerified     true once the guest has confirmed their email
     */
    public Guest(String guestId, String firstName, String lastName,
                 String email, String passwordHash,
                 String verificationToken, boolean emailVerified) {
        this.guestId           = guestId;
        this.firstName         = firstName;
        this.lastName          = lastName;
        this.email             = email;
        this.passwordHash      = passwordHash;
        this.verificationToken = verificationToken;
        this.emailVerified     = emailVerified;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    /** @return UUID primary key for this guest */
    public String getGuestId()           { return guestId; }

    /** @return guest's first name */
    public String getFirstName()         { return firstName; }

    /** @return guest's last name */
    public String getLastName()          { return lastName; }

    /** @return guest's email address */
    public String getEmail()             { return email; }

    /** @return BCrypt password hash — never expose the plaintext */
    public String getPasswordHash()      { return passwordHash; }

    /** @return UUID verification token pending email confirmation */
    public String getVerificationToken() { return verificationToken; }

    /** @return true if the guest has confirmed their email address */
    public boolean isEmailVerified()     { return emailVerified; }

    // ── Setters ───────────────────────────────────────────────────────────────

    /**
     * Sets the email verification status.
     * Called by PersistenceService after a successful token confirmation.
     *
     * @param emailVerified true once verification is confirmed
     */
    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    @Override
    public String toString() {
        return "Guest{guestId='" + guestId + "', email='" + email +
               "', verified=" + emailVerified + "}";
    }
}