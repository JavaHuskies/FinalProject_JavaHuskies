package model;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for Claims.
 * Verifies role constant values and permission helper logic.
 *
 * @author John Baldwin
 */
public class ClaimsTest {

    // ── Role constant values ──────────────────────────────────────────────────

    @Test
    public void testRoleConstantsCamelCase() {
        // Verify all role constants use camelCase values — these are stored in DB and JWTs
        assertEquals("networkAdmin",      Claims.roleNetworkAdmin);
        assertEquals("systemAdmin",       Claims.roleSystemAdmin);
        assertEquals("enterpriseAdmin",   Claims.roleEnterpriseAdmin);
        assertEquals("groupCeo",          Claims.roleGroupCeo);
        assertEquals("groupCfo",          Claims.roleGroupCfo);
        assertEquals("entPresident",      Claims.roleEntPresident);
        assertEquals("entCoo",            Claims.roleEntCoo);
        assertEquals("orgDirector",       Claims.roleOrgDirector);
        assertEquals("creativeLead",      Claims.roleCreativeLead);
        assertEquals("technologyLead",    Claims.roleTechnologyLead);
        assertEquals("marketingLead",     Claims.roleMarketingLead);
        assertEquals("complianceOfficer", Claims.roleComplianceOfficer);
        assertEquals("dataAnalyst",       Claims.roleDataAnalyst);
        assertEquals("guest",             Claims.roleGuest);
    }

    // ── isNetworkAdmin ────────────────────────────────────────────────────────

    @Test
    public void testIsNetworkAdminTrueForNetworkAdmin() {
        assertTrue(Claims.isNetworkAdmin(Claims.roleNetworkAdmin));
    }

    @Test
    public void testIsNetworkAdminFalseForOrgDirector() {
        assertFalse(Claims.isNetworkAdmin(Claims.roleOrgDirector));
    }

    // ── isEnterpriseAdmin ─────────────────────────────────────────────────────

    @Test
    public void testIsEnterpriseAdminTrueForEnterpriseAdmin() {
        assertTrue(Claims.isEnterpriseAdmin(Claims.roleEnterpriseAdmin));
    }

    @Test
    public void testIsEnterpriseAdminTrueForEntPresident() {
        assertTrue(Claims.isEnterpriseAdmin(Claims.roleEntPresident));
    }

    @Test
    public void testIsEnterpriseAdminFalseForOrgDirector() {
        assertFalse(Claims.isEnterpriseAdmin(Claims.roleOrgDirector));
    }

    // ── canSubmitWorkRequests ─────────────────────────────────────────────────

    @Test
    public void testCanSubmitWorkRequestsTrueForOrgDirector() {
        assertTrue(Claims.canSubmitWorkRequests(Claims.roleOrgDirector));
    }

    @Test
    public void testCanSubmitWorkRequestsTrueForCreativeLead() {
        assertTrue(Claims.canSubmitWorkRequests(Claims.roleCreativeLead));
    }

    @Test
    public void testCanSubmitWorkRequestsTrueForTechnologyLead() {
        assertTrue(Claims.canSubmitWorkRequests(Claims.roleTechnologyLead));
    }

    @Test
    public void testCanSubmitWorkRequestsTrueForMarketingLead() {
        assertTrue(Claims.canSubmitWorkRequests(Claims.roleMarketingLead));
    }

    @Test
    public void testCanSubmitWorkRequestsFalseForDataAnalyst() {
        assertFalse(Claims.canSubmitWorkRequests(Claims.roleDataAnalyst));
    }

    @Test
    public void testCanSubmitWorkRequestsFalseForGuest() {
        assertFalse(Claims.canSubmitWorkRequests(Claims.roleGuest));
    }

    @Test
    public void testCanSubmitWorkRequestsFalseForNull() {
        assertFalse(Claims.canSubmitWorkRequests(null));
    }

    // ── canApproveWorkRequests ────────────────────────────────────────────────

    @Test
    public void testCanApproveWorkRequestsTrueForEnterpriseAdmin() {
        assertTrue(Claims.canApproveWorkRequests(Claims.roleEnterpriseAdmin));
    }

    @Test
    public void testCanApproveWorkRequestsTrueForOrgDirector() {
        assertTrue(Claims.canApproveWorkRequests(Claims.roleOrgDirector));
    }

    @Test
    public void testCanApproveWorkRequestsFalseForCreativeLead() {
        assertFalse(Claims.canApproveWorkRequests(Claims.roleCreativeLead));
    }

    // ── canAccessReports ──────────────────────────────────────────────────────

    @Test
    public void testCanAccessReportsTrueForNetworkAdmin() {
        assertTrue(Claims.canAccessReports(Claims.roleNetworkAdmin));
    }

    @Test
    public void testCanAccessReportsTrueForDataAnalyst() {
        assertTrue(Claims.canAccessReports(Claims.roleDataAnalyst));
    }

    @Test
    public void testCanAccessReportsTrueForComplianceOfficer() {
        assertTrue(Claims.canAccessReports(Claims.roleComplianceOfficer));
    }

    @Test
    public void testCanAccessReportsFalseForCreativeLead() {
        assertFalse(Claims.canAccessReports(Claims.roleCreativeLead));
    }

    @Test
    public void testCanAccessReportsFalseForGuest() {
        assertFalse(Claims.canAccessReports(Claims.roleGuest));
    }

    @Test
    public void testCanAccessReportsFalseForNull() {
        assertFalse(Claims.canAccessReports(null));
    }
}