package service;

import model.Claims;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for SessionManager.
 * Covers login/logout, claim accessors, guard methods, and demo session injection.
 *
 * @author John Baldwin
 */
public class SessionManagerTest {

    private AuthService auth;

    @Before
    public void setUp() {
        auth = AuthService.getInstance();
        SessionManager.logout(); // ensure clean state before each test
    }

    @After
    public void tearDown() {
        SessionManager.logout(); // clean up after each test
    }

    // ── Login / logout ────────────────────────────────────────────────────────

    @Test
    public void testIsLoggedInFalseBeforeLogin() {
        assertFalse("Should not be logged in before login", SessionManager.isLoggedIn());
    }

    @Test
    public void testIsLoggedInTrueAfterLogin() {
        loginAs("netadmin", "networkAdmin", "slartibartfastPictures", "magratheaStudios");
        assertTrue("Should be logged in after login", SessionManager.isLoggedIn());
    }

    @Test
    public void testIsLoggedInFalseAfterLogout() {
        loginAs("netadmin", "networkAdmin", "slartibartfastPictures", "magratheaStudios");
        SessionManager.logout();
        assertFalse("Should not be logged in after logout", SessionManager.isLoggedIn());
    }

    // ── Claim accessors ───────────────────────────────────────────────────────

    @Test
    public void testGetRoleReturnsCorrectRole() {
        loginAs("netadmin", "networkAdmin", "slartibartfastPictures", "magratheaStudios");
        assertEquals("networkAdmin", SessionManager.getRole());
    }

    @Test
    public void testGetUserIdReturnsCorrectId() {
        loginAs("netadmin", "networkAdmin", "slartibartfastPictures", "magratheaStudios");
        assertEquals("netadmin", SessionManager.getUserId());
    }

    @Test
    public void testGetOrgIdReturnsCorrectOrg() {
        loginAs("netadmin", "networkAdmin", "slartibartfastPictures", "magratheaStudios");
        assertEquals("slartibartfastPictures", SessionManager.getOrgId());
    }

    @Test
    public void testGetEnterpriseIdReturnsCorrectEnterprise() {
        loginAs("netadmin", "networkAdmin", "slartibartfastPictures", "magratheaStudios");
        assertEquals("magratheaStudios", SessionManager.getEnterpriseId());
    }

    @Test
    public void testGetRoleNullWhenNotLoggedIn() {
        assertNull("Role should be null when not logged in", SessionManager.getRole());
    }

    @Test
    public void testGetUserIdNullWhenNotLoggedIn() {
        assertNull("UserId should be null when not logged in", SessionManager.getUserId());
    }

    // ── Guard methods ─────────────────────────────────────────────────────────

    @Test
    public void testGuardReturnsTrueForMatchingRole() {
        loginAs("netadmin", "networkAdmin", "slartibartfastPictures", "magratheaStudios");
        assertTrue("guard() should return true for matching role",
                   SessionManager.guard(Claims.roleNetworkAdmin));
    }

    @Test
    public void testGuardReturnsFalseForNonMatchingRole() {
        loginAs("netadmin", "networkAdmin", "slartibartfastPictures", "magratheaStudios");
        assertFalse("guard() should return false for non-matching role",
                    SessionManager.guard(Claims.roleOrgDirector));
    }

    @Test
    public void testGuardAnyReturnsTrueWhenOneRoleMatches() {
        loginAs("grpceo", "groupCeo", "slartibartfastPictures", "magratheaStudios");
        assertTrue("guardAny() should return true when one role matches",
                   SessionManager.guardAny(Claims.roleNetworkAdmin, Claims.roleGroupCeo));
    }

    @Test
    public void testGuardAnyReturnsFalseWhenNoRoleMatches() {
        loginAs("grpceo", "groupCeo", "slartibartfastPictures", "magratheaStudios");
        assertFalse("guardAny() should return false when no role matches",
                    SessionManager.guardAny(Claims.roleOrgDirector, Claims.roleDataAnalyst));
    }

    @Test
    public void testGuardReturnsFalseWhenNotLoggedIn() {
        assertFalse("guard() should return false when not logged in",
                    SessionManager.guard(Claims.roleNetworkAdmin));
    }

    // ── Permission helpers ────────────────────────────────────────────────────

    @Test
    public void testCanSubmitWorkRequestsTrueForOrgDirector() {
        loginAs("orgdir1", "orgDirector", "infiniteImprobabilityStreaming", "galacticBroadcasting");
        assertTrue(SessionManager.canSubmitWorkRequests());
    }

    @Test
    public void testCanSubmitWorkRequestsFalseForDataAnalyst() {
        loginAs("analyst1", "dataAnalyst", "hooloovooRetail", "siriusCybernetics");
        assertFalse(SessionManager.canSubmitWorkRequests());
    }

    @Test
    public void testCanApproveWorkRequestsTrueForEnterpriseAdmin() {
        loginAs("entadmin", "enterpriseAdmin", "magratheaThemeWorlds", "starshipTitanicLeisure");
        assertTrue(SessionManager.canApproveWorkRequests());
    }

    @Test
    public void testCanAccessReportsTrueForNetworkAdmin() {
        loginAs("netadmin", "networkAdmin", "slartibartfastPictures", "magratheaStudios");
        assertTrue(SessionManager.canAccessReports());
    }

    @Test
    public void testCanAccessReportsFalseForCreativeLead() {
        loginAs("creative1", "creativeLead", "bistromathAnimation", "magratheaStudios");
        assertFalse(SessionManager.canAccessReports());
    }

    // ── Demo session injection ────────────────────────────────────────────────

    @Test
    public void testInjectDemoSessionSetsRole() {
        SessionManager.injectDemoSession("orgDirector",
                                         "bistromathAnimation", "magratheaStudios");
        assertTrue("Should be logged in after demo injection", SessionManager.isLoggedIn());
        assertEquals("orgDirector", SessionManager.getRole());
    }

    @Test
    public void testInjectDemoSessionSetsEnterprise() {
        SessionManager.injectDemoSession("orgDirector",
                                         "bistromathAnimation", "magratheaStudios");
        assertEquals("magratheaStudios", SessionManager.getEnterpriseId());
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private void loginAs(String userId, String role, String orgId, String enterpriseId) {
        String token = auth.issueJWT(userId, role, orgId, enterpriseId);
        model.Claims claims = auth.validateJWT(token);
        SessionManager.login(token, claims);
    }
}