package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Represents a registered guest user.
 * Created by {@link service.AuthService#registerGuest} and persisted via
 * PersistenceService (pending). A guest cannot log in until
 * {@code emailVerified} is set to {@code true} via
 * {@link service.AuthService#confirmVerification}.
 *
 * <p>Fields map to the GUEST table in schema.sql.
 * Setters for mutable fields (emailVerified) are provided for PersistenceService
 * to update the record after verification.
 */
@DatabaseTable(tableName = "guest")
public class Guest {

    @DatabaseField(id = true, columnName = "guest_id", canBeNull = false)
    private String guestId;

    @DatabaseField(columnName = "first_name")
    private String firstName;

    @DatabaseField(columnName = "last_name")
    private String lastName;

    @DatabaseField(unique = true, canBeNull = false)
    private String email;

    @DatabaseField(columnName = "password_hash", canBeNull = false)
    private String passwordHash;

    @DatabaseField
    private String phone;

    @DatabaseField(columnName = "loyalty_points", canBeNull = false)
    private int loyaltyPoints;

    // Stored as 0/1 integer in SQLite. True once the guest confirms their email.
    @DatabaseField(columnName = "is_verified", canBeNull = false)
    private boolean emailVerified;

    @DatabaseField(columnName = "verification_token")
    private String verificationToken;

    @DatabaseField(columnName = "jwt_token")
    private String jwtToken;

    @DatabaseField(columnName = "created_at", canBeNull = false)
    private String createdAt;

    /** Required by ORMLite. */
    public Guest() {}

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

    /** @return guest's phone number */
    public String getPhone()             { return phone; }

    /** @return accumulated loyalty points */
    public int getLoyaltyPoints()        { return loyaltyPoints; }

    /** @return UUID verification token pending email confirmation */
    public String getVerificationToken() { return verificationToken; }

    /** @return true if the guest has confirmed their email address */
    public boolean isEmailVerified()     { return emailVerified; }

    /** @return JWT token for the current session */
    public String getJwtToken()          { return jwtToken; }

    /** @return ISO-8601 creation timestamp */
    public String getCreatedAt()         { return createdAt; }

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

    public void setPhone(String phone)             { this.phone = phone; }
    public void setLoyaltyPoints(int points)       { this.loyaltyPoints = points; }
    public void setJwtToken(String jwtToken)       { this.jwtToken = jwtToken; }
    public void setCreatedAt(String createdAt)     { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Guest{guestId='" + guestId + "', email='" + email +
               "', verified=" + emailVerified + "}";
    }
}
