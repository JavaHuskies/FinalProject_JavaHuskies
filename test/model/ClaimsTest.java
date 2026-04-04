package model;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Date;

/**
 * Unit tests for Claims.
 * Verifies role constant values and permission helper logic.
 *
 * @author John Baldwin
 */
public class ClaimsTest {

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Builds a valid, non-expired Claims instance for the given role.
     *
     * @param role role string to assign
     * @return Claims instance valid for 1 hour
     */
    private Claims claims(String role) {
        return new Claims(
            "test-user", role,
            "slartibartfastPictures", "magratheaStudios",
            "test@deepthought.com",
            new Date(),
            new Date(System.currentTimeMillis() + 3_600_000L)
        );
    }

    /**
     * Builds an expired Claims instance for the given role.
     *
     * @param role role string to assign
     * @return Claims instance already expired
     */
    private Claims expiredClaims(String role) {
        return new Claims(
            "test-user", role,
            "slartibartfastPictures", "magratheaStudios",
            "test@deepthought.com",
            new Date(System.currentTimeMillis() - 7_200_000L),
            new Date(System.currentTimeMillis() - 3_600_000L)
        );
    }

    // ── Role constant values ──────────────────────────────────────────────────

    @Test
    public void testRoleConstantsCamelCase() {
        assertEquals("networkAdmin",       Claims.roleNetworkAdmin);
        assertEquals("systemAdmin",        Claims.roleSystemAdmin);
        assertEquals("enterpriseAdmin",    Claims.roleEnterpriseAdmin);
        assertEquals("groupCeo",           Claims.roleGroupCeo);
        assertEquals("groupCfo",           Claims.roleGroupCfo);
        assertEquals("enterprisePresident",Claims.roleEntPresident);
        assertEquals("enterpriseCoo",      Claims.roleEntCoo);
        assertEquals("orgDirector",        Claims.roleOrgDirector);
        assertEquals("creativeLead",       Claims.roleCreativeLead);
        assertEquals("technologyLead",     Claims.roleTechnologyLead);
        assertEquals("marketingLead",      Claims.roleMarketingLead);
        assertEquals("complianceOfficer",  Claims.roleComplianceOfficer);
        assertEquals("dataAnalyst",        Claims.roleDataAnalyst);
        assertEquals("guest",              Claims.roleGuest);
    }

    // ── isValid ───────────────────────────────────────────────────────────────

    @Test
    public void testIsValidTrueForNonExpiredToken() {
        assertTrue("Non-expired claims should be valid",
                claims(Claims.roleNetworkAdmin).isValid());
    }

    @Test
    public void testIsValidFalseForExpiredToken() {
        assertFalse("Expired claims should not be valid",
                expiredClaims(Claims.roleNetworkAdmin).isValid());
    }

    // ── isGuest ───────────────────────────────────────────────────────────────

    @Test
    public void testIsGuestTrueForGuestRole() {
        assertTrue("Guest role should return isGuest true",
                claims(Claims.roleGuest).isGuest());
    }

    @Test
    public void testIsGuestFalseForStaffRole() {
        assertFalse("Staff role should return isGuest false",
                claims(Claims.roleOrgDirector).isGuest());
    }

    // ── isNetworkAdmin ────────────────────────────────────────────────────────

    @Test
    public void testIsNetworkAdminTrueForNetworkAdmin() {
        assertTrue("networkAdmin should return isNetworkAdmin true",
                claims(Claims.roleNetworkAdmin).isNetworkAdmin());
    }

    @Test
    public void testIsNetworkAdminTrueForSystemAdmin() {
        assertTrue("systemAdmin should return isNetworkAdmin true",
                claims(Claims.roleSystemAdmin).isNetworkAdmin());
    }

    @Test
    public void testIsNetworkAdminFalseForOrgDirector() {
        assertFalse("orgDirector should return isNetworkAdmin false",
                claims(Claims.roleOrgDirector).isNetworkAdmin());
    }

    @Test
    public void testIsNetworkAdminFalseForGuest() {
        assertFalse("guest should return isNetworkAdmin false",
                claims(Claims.roleGuest).isNetworkAdmin());
    }

    // ── isEnterpriseAdmin ─────────────────────────────────────────────────────

    @Test
    public void testIsEnterpriseAdminTrueForEnterpriseAdmin() {
        assertTrue("enterpriseAdmin should return isEnterpriseAdmin true",
                claims(Claims.roleEnterpriseAdmin).isEnterpriseAdmin());
    }

    @Test
    public void testIsEnterpriseAdminTrueForGroupCeo() {
        assertTrue("groupCeo should return isEnterpriseAdmin true",
                claims(Claims.roleGroupCeo).isEnterpriseAdmin());
    }

    @Test
    public void testIsEnterpriseAdminTrueForGroupCfo() {
        assertTrue("groupCfo should return isEnterpriseAdmin true",
                claims(Claims.roleGroupCfo).isEnterpriseAdmin());
    }

    @Test
    public void testIsEnterpriseAdminTrueForNetworkAdmin() {
        assertTrue("networkAdmin should return isEnterpriseAdmin true via isNetworkAdmin",
                claims(Claims.roleNetworkAdmin).isEnterpriseAdmin());
    }

    @Test
    public void testIsEnterpriseAdminFalseForOrgDirector() {
        assertFalse("orgDirector should return isEnterpriseAdmin false",
                claims(Claims.roleOrgDirector).isEnterpriseAdmin());
    }

    @Test
    public void testIsEnterpriseAdminFalseForGuest() {
        assertFalse("guest should return isEnterpriseAdmin false",
                claims(Claims.roleGuest).isEnterpriseAdmin());
    }

    // ── canSubmitWorkRequests ─────────────────────────────────────────────────

    @Test
    public void testCanSubmitWorkRequestsTrueForOrgDirector() {
        assertTrue("orgDirector should be able to submit work requests",
                claims(Claims.roleOrgDirector).canSubmitWorkRequests());
    }

    @Test
    public void testCanSubmitWorkRequestsTrueForCreativeLead() {
        assertTrue("creativeLead should be able to submit work requests",
                claims(Claims.roleCreativeLead).canSubmitWorkRequests());
    }

    @Test
    public void testCanSubmitWorkRequestsTrueForTechnologyLead() {
        assertTrue("technologyLead should be able to submit work requests",
                claims(Claims.roleTechnologyLead).canSubmitWorkRequests());
    }

    @Test
    public void testCanSubmitWorkRequestsTrueForMarketingLead() {
        assertTrue("marketingLead should be able to submit work requests",
                claims(Claims.roleMarketingLead).canSubmitWorkRequests());
    }

    @Test
    public void testCanSubmitWorkRequestsFalseForGuest() {
        assertFalse("guest should not be able to submit work requests",
                claims(Claims.roleGuest).canSubmitWorkRequests());
    }

    @Test
    public void testCanSubmitWorkRequestsFalseForExpiredToken() {
        assertFalse("Expired claims should not be able to submit work requests",
                expiredClaims(Claims.roleOrgDirector).canSubmitWorkRequests());
    }

    // ── canApproveWorkRequests ────────────────────────────────────────────────

    @Test
    public void testCanApproveWorkRequestsTrueForOrgDirector() {
        assertTrue("orgDirector should be able to approve work requests",
                claims(Claims.roleOrgDirector).canApproveWorkRequests());
    }

    @Test
    public void testCanApproveWorkRequestsTrueForEntPresident() {
        assertTrue("enterprisePresident should be able to approve work requests",
                claims(Claims.roleEntPresident).canApproveWorkRequests());
    }

    @Test
    public void testCanApproveWorkRequestsTrueForEntCoo() {
        assertTrue("enterpriseCoo should be able to approve work requests",
                claims(Claims.roleEntCoo).canApproveWorkRequests());
    }

    @Test
    public void testCanApproveWorkRequestsTrueForEnterpriseAdmin() {
        assertTrue("enterpriseAdmin should be able to approve work requests",
                claims(Claims.roleEnterpriseAdmin).canApproveWorkRequests());
    }

    @Test
    public void testCanApproveWorkRequestsFalseForCreativeLead() {
        assertFalse("creativeLead should not be able to approve work requests",
                claims(Claims.roleCreativeLead).canApproveWorkRequests());
    }

    @Test
    public void testCanApproveWorkRequestsFalseForGuest() {
        assertFalse("guest should not be able to approve work requests",
                claims(Claims.roleGuest).canApproveWorkRequests());
    }

    @Test
    public void testCanApproveWorkRequestsFalseForExpiredToken() {
        assertFalse("Expired claims should not be able to approve work requests",
                expiredClaims(Claims.roleOrgDirector).canApproveWorkRequests());
    }

    // ── canAccessReports ──────────────────────────────────────────────────────

    @Test
    public void testCanAccessReportsTrueForNetworkAdmin() {
        assertTrue("networkAdmin should be able to access reports",
                claims(Claims.roleNetworkAdmin).canAccessReports());
    }

    @Test
    public void testCanAccessReportsTrueForDataAnalyst() {
        assertTrue("dataAnalyst should be able to access reports",
                claims(Claims.roleDataAnalyst).canAccessReports());
    }

    @Test
    public void testCanAccessReportsTrueForComplianceOfficer() {
        assertTrue("complianceOfficer should be able to access reports",
                claims(Claims.roleComplianceOfficer).canAccessReports());
    }

    @Test
    public void testCanAccessReportsTrueForCreativeLead() {
        assertTrue("creativeLead should be able to access reports — all valid non-guest staff can",
                claims(Claims.roleCreativeLead).canAccessReports());
    }

    @Test
    public void testCanAccessReportsFalseForGuest() {
        assertFalse("guest should not be able to access reports",
                claims(Claims.roleGuest).canAccessReports());
    }

    @Test
    public void testCanAccessReportsFalseForExpiredToken() {
        assertFalse("Expired claims should not be able to access reports",
                expiredClaims(Claims.roleNetworkAdmin).canAccessReports());
    }

    // ── getters ───────────────────────────────────────────────────────────────

    @Test
    public void testGettersReturnCorrectValues() {
        Claims c = claims(Claims.roleOrgDirector);
        assertEquals("test-user",               c.getUserId());
        assertEquals(Claims.roleOrgDirector,     c.getRole());
        assertEquals("slartibartfastPictures",   c.getOrgId());
        assertEquals("magratheaStudios",         c.getEnterpriseId());
        assertEquals("test@deepthought.com",     c.getEmail());
        assertNotNull("issuedAt should not be null",    c.getIssuedAt());
        assertNotNull("expiration should not be null",  c.getExpiration());
    }
}