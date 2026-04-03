package service;

import model.Claims;
import java.util.logging.Logger;

/**
 * Static session holder — stores the active JWT token and decoded Claims
 * for the current user session. All panels access the current session
 * through this class rather than passing tokens around.
 *
 * Thread note: This is a single-user desktop application. No synchronization
 * is required — only one session exists at a time.
 *
 * Usage:
 *   // On successful login:
 *   SessionManager.login(token, claims);
 *
 *   // In any panel — get current user's role:
 *   Claims c = SessionManager.getClaims();
 *   if (c != null && c.isValid()) {
 *       String role = c.getRole();
 *   }
 *
 *   // On logout:
 *   SessionManager.logout();
 *
 *   // Quick role checks:
 *   if (SessionManager.isLoggedIn()) { ... }
 *   if (SessionManager.hasRole(Claims.ROLE_ORG_DIRECTOR)) { ... }
 *   if (SessionManager.isGuest()) { ... }
 */
public class SessionManager {

    private static final Logger LOG = Logger.getLogger(SessionManager.class.getName());

    private static String  activeToken;
    private static Claims  activeClaims;
    private static long    loginTimestamp;

    // Prevent instantiation — this is a static utility class
    private SessionManager() {}

    // -------------------------------------------------------------------------
    // Session lifecycle
    // -------------------------------------------------------------------------

    /**
     * Establishes a new session after successful authentication.
     * Call this from the login panel after AuthService.validateJWT() succeeds.
     *
     * @param token  signed JWT string from AuthService.issueJWT()
     * @param claims decoded Claims from AuthService.validateJWT()
     */
    public static void login(String token, Claims claims) {
        if (token == null || claims == null || !claims.isValid()) {
            LOG.warning("SessionManager.login() called with invalid token or claims");
            return;
        }
        activeToken    = token;
        activeClaims   = claims;
        loginTimestamp = System.currentTimeMillis();
        LOG.info("Session started — userId: " + claims.getUserId()
                 + ", role: " + claims.getRole()
                 + ", org: " + claims.getOrgId());
    }

    /**
     * Clears the current session and revokes the token.
     * Call this on logout or session expiry.
     */
    public static void logout() {
        if (activeToken != null) {
            AuthService.getInstance().revokeToken(activeToken);
            LOG.info("Session ended — userId: "
                     + (activeClaims != null ? activeClaims.getUserId() : "unknown"));
        }
        activeToken    = null;
        activeClaims   = null;
        loginTimestamp = 0;
    }

    // -------------------------------------------------------------------------
    // Session state
    // -------------------------------------------------------------------------

    /** Returns true if a valid session is active */
    public static boolean isLoggedIn() {
        return activeToken != null
            && activeClaims != null
            && activeClaims.isValid();
    }

    /** Returns the active Claims, or null if no session exists */
    public static Claims getClaims() {
        return activeClaims;
    }

    /** Returns the active JWT token string, or null if no session exists */
    public static String getToken() {
        return activeToken;
    }

    /** Returns the epoch timestamp of the current login, or 0 if not logged in */
    public static long getLoginTimestamp() {
        return loginTimestamp;
    }

    /** Returns how many milliseconds the current session has been active */
    public static long getSessionDurationMs() {
        if (loginTimestamp == 0) return 0;
        return System.currentTimeMillis() - loginTimestamp;
    }

    // -------------------------------------------------------------------------
    // Convenience role checks — use these in panels instead of string comparison
    // -------------------------------------------------------------------------

    /** Returns the current user's role string, or null if not logged in */
    public static String getRole() {
        return activeClaims != null ? activeClaims.getRole() : null;
    }

    /** Returns the current user's userId, or null if not logged in */
    public static String getUserId() {
        return activeClaims != null ? activeClaims.getUserId() : null;
    }

    /** Returns the current user's orgId, or null if not logged in */
    public static String getOrgId() {
        return activeClaims != null ? activeClaims.getOrgId() : null;
    }

    /** Returns the current user's enterpriseId, or null if not logged in */
    public static String getEnterpriseId() {
        return activeClaims != null ? activeClaims.getEnterpriseId() : null;
    }

    /** Returns the current user's email, or null if not logged in */
    public static String getEmail() {
        return activeClaims != null ? activeClaims.getEmail() : null;
    }

    /**
    * Returns true if the current user has exactly the specified role.
    *
    * @param role role string to test — use Claims role constants
    * @return true if the current session role matches exactly
    */
    public static boolean hasRole(String role) {
        return role != null && role.equals(getRole());
    }

    /**
    * Returns true if the current user has any of the specified roles.
    *
    * @param roles one or more role strings to test — use Claims role constants
    * @return true if the current session role matches any entry in roles
    */
    public static boolean hasAnyRole(String... roles) {
        String current = getRole();
        if (current == null) return false;
        for (String r : roles) {
            if (current.equals(r)) return true;
        }
        return false;
    }

    /** Returns true if the current session belongs to a guest */
    public static boolean isGuest() {
        return activeClaims != null && activeClaims.isGuest();
    }

    /** Returns true if the current session belongs to a network/system admin */
    public static boolean isNetworkAdmin() {
        return activeClaims != null && activeClaims.isNetworkAdmin();
    }

    /** Returns true if the current session belongs to an enterprise admin or above */
    public static boolean isEnterpriseAdmin() {
        return activeClaims != null && activeClaims.isEnterpriseAdmin();
    }

    /** Returns true if the current user can submit work requests */
    public static boolean canSubmitWorkRequests() {
        return activeClaims != null && activeClaims.canSubmitWorkRequests();
    }

    /** Returns true if the current user can approve or reject work requests */
    public static boolean canApproveWorkRequests() {
        return activeClaims != null && activeClaims.canApproveWorkRequests();
    }

    /** Returns true if the current user can access reports */
    public static boolean canAccessReports() {
        return activeClaims != null && activeClaims.canAccessReports();
    }

    // -------------------------------------------------------------------------
    // Demo mode support
    // -------------------------------------------------------------------------

    /**
     * Injects a demo session for a given role without requiring database lookup.
     * Triggered by the Ctrl+Shift+D demo mode switcher in MainShell.
     * Only available when app.demo.mode.enabled=true in config.properties.
     *
     * @param role        one of the Claims.ROLE_* constants
     * @param orgId       org to associate with the demo session
     * @param enterpriseId enterprise to associate with the demo session
     */
    public static void injectDemoSession(String role, String orgId, String enterpriseId) {
        if (!ConfigService.getInstance().getBool("app.demo.mode.enabled", false)) {
            LOG.warning("Demo mode is disabled — ignoring injectDemoSession()");
            return;
        }
        String demoToken = AuthService.getInstance().issueJWT(
            "DEMO-USER", role, orgId, enterpriseId, "demo@deepthought.com"
        );
        Claims demoClaims = AuthService.getInstance().validateJWT(demoToken);
        login(demoToken, demoClaims);
        LOG.info("Demo session injected — role: " + role);
    }

    // -------------------------------------------------------------------------
    // Session validation guard
    // -------------------------------------------------------------------------

    /**
     * Panel guard — call at the top of every panel's initialize() method.
     * Returns true if the session is valid and the user has the required role.
     * Returns false if the session is invalid or the role does not match.
     *
     * Usage:
     *   if (!SessionManager.guard(Claims.ROLE_ORG_DIRECTOR)) {
     *       mainShell.showPanel("login");
     *       return;
     *   }
     *
     * @param requiredRole the Claims.ROLE_* constant required for this panel
     * @return true if access is permitted
     */
    public static boolean guard(String requiredRole) {
        if (!isLoggedIn()) {
            LOG.warning("Guard failed — no active session");
            return false;
        }
        if (!hasRole(requiredRole)) {
            LOG.warning("Guard failed — required: " + requiredRole
                        + ", actual: " + getRole());
            return false;
        }
        return true;
    }

    /**
    * Multi-role guard — returns true if the session is valid and the user
    * has any of the specified roles.
    *
    * Usage:
    *   if (!SessionManager.guardAny(Claims.ROLE_NETWORK_ADMIN,
    *                                Claims.ROLE_ENTERPRISE_ADMIN)) {
    *       mainShell.showPanel("login");
    *       return;
    *   }
    *
    * @param requiredRoles one or more Claims role constants — access granted if any match
    * @return true if the session is active and the user holds at least one required role
    */
    public static boolean guardAny(String... requiredRoles) {
        if (!isLoggedIn()) {
            LOG.warning("Guard failed — no active session");
            return false;
        }
        if (!hasAnyRole(requiredRoles)) {
            LOG.warning("Guard failed — role " + getRole()
                        + " not in required set");
            return false;
        }
        return true;
    }
public static void injectGuestSession(String username) {
    injectDemoSession(
        Claims.roleGuest,
        "guestOrg",
        "guestEnterprise"
    );
}

}