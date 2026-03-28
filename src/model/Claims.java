package model;

import java.util.Date;

/**
 * Represents the decoded payload of a JWT token.
 * Returned by AuthService.validateJWT() and passed to panels
 * for role-based access control decisions.
 *
 * Usage:
 *   Claims claims = AuthService.getInstance().validateJWT(token);
 *   if (claims != null && claims.isValid()) {
 *       String role = claims.getRole();
 *       // route to correct panel
 *   }
 */
public class Claims {

    // Role constants — match role field values in USER and GUEST tables
    public static final String roleNetworkAdmin      = "networkAdmin";
    public static final String roleSystemAdmin       = "systemAdmin";
    public static final String roleEnterpriseAdmin   = "enterpriseAdmin";
    public static final String roleGroupCeo          = "groupCeo";
    public static final String roleGroupCfo          = "groupCfo";
    public static final String roleEntPresident      = "enterprisePresident";
    public static final String roleEntCoo            = "enterpriseCoo";
    public static final String roleOrgDirector       = "orgDirector";
    public static final String roleCreativeLead      = "creativeLead";
    public static final String roleTechnologyLead    = "technologyLead";
    public static final String roleMarketingLead     = "marketingLead";
    public static final String roleComplianceOfficer = "complianceOfficer";
    public static final String roleDataAnalyst       = "dataAnalyst";
    public static final String roleGuest             = "guest";

    private final String userId;
    private final String role;
    private final String orgId;
    private final String enterpriseId;
    private final String email;
    private final Date issuedAt;
    private final Date expiration;

    public Claims(String userId, String role, String orgId,
                  String enterpriseId, String email,
                  Date issuedAt, Date expiration) {
        this.userId       = userId;
        this.role         = role;
        this.orgId        = orgId;
        this.enterpriseId = enterpriseId;
        this.email        = email;
        this.issuedAt     = issuedAt;
        this.expiration   = expiration;
    }

    // -------------------------------------------------------------------------
    // Validity
    // -------------------------------------------------------------------------

    /** Returns true if the token exists and has not expired */
    public boolean isValid() {
        return expiration != null && expiration.after(new Date());
    }

    /** Returns true if this claims object represents an authenticated guest */
    public boolean isGuest() {
        return roleGuest.equals(role);
    }

    /** Returns true if this user has network-level admin access */
    public boolean isNetworkAdmin() {
        return roleNetworkAdmin.equals(role) || roleSystemAdmin.equals(role);
    }

    /** Returns true if this user has enterprise-level admin access */
    public boolean isEnterpriseAdmin() {
        return roleEnterpriseAdmin.equals(role)
            || roleGroupCeo.equals(role)
            || roleGroupCfo.equals(role)
            || isNetworkAdmin();
    }

    /** Returns true if this user can submit work requests */
    public boolean canSubmitWorkRequests() {
        return !isGuest() && isValid();
    }

    /** Returns true if this user can approve or reject work requests */
    public boolean canApproveWorkRequests() {
        return isValid() && (
            roleOrgDirector.equals(role)    ||
            roleEntPresident.equals(role)   ||
            roleEntCoo.equals(role)         ||
            isEnterpriseAdmin()
        );
    }

    /** Returns true if this user can access reports */
    public boolean canAccessReports() {
        return isValid() && !isGuest();
    }

    /** Returns true if this user can access network-level data */
    public boolean canAccessNetworkData() {
        return isValid() && isNetworkAdmin();
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public String getUserId()       { return userId; }
    public String getRole()         { return role; }
    public String getOrgId()        { return orgId; }
    public String getEnterpriseId() { return enterpriseId; }
    public String getEmail()        { return email; }
    public Date getIssuedAt()       { return issuedAt; }
    public Date getExpiration()     { return expiration; }

    @Override
    public String toString() {
        return "Claims{userId='" + userId + "', role='" + role +
               "', orgId='" + orgId + "', valid=" + isValid() + "}";
    }
}