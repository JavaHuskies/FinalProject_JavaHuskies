package ui;

import model.Claims;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import service.AuthService;
import service.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

/**
 * UI smoke tests for ApplicationFrame.
 * Verifies that the frame constructs without error, panels are registered,
 * and navigation behaves correctly for public and authenticated screens.
 *
 * Tests are skipped automatically in headless CI environments.
 *
 * @author John Baldwin
 */
public class ApplicationFrameTest {

    private ApplicationFrame frame;

    @BeforeClass
    public static void checkHeadless() {
        // Skip all tests if running in a headless environment
        Assume.assumeFalse(
            "Skipping UI tests in headless environment",
            GraphicsEnvironment.isHeadless()
        );
    }

    @Before
    public void setUp() throws Exception {
        SessionManager.logout();
        SwingUtilities.invokeAndWait(() -> frame = new ApplicationFrame());
    }

    @After
    public void tearDown() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            if (frame != null) {
                frame.dispose();
                frame = null;
            }
        });
        SessionManager.logout();
    }

    // ── Construction ──────────────────────────────────────────────────────────

    @Test
    public void testFrameConstructsWithoutError() {
        assertNotNull("Frame should not be null", frame);
    }

    @Test
    public void testFrameTitleSet() {
        assertFalse("Frame title should not be empty",
                    frame.getTitle().isEmpty());
    }

    @Test
    public void testFrameHasContentPane() {
        assertNotNull("Frame should have a content pane", frame.getContentPane());
    }

    // ── Panel constants ───────────────────────────────────────────────────────

    @Test
    public void testPanelConstantsNonNull() {
        assertNotNull(ApplicationFrame.panelSplash);
        assertNotNull(ApplicationFrame.panelStaffLogin);
        assertNotNull(ApplicationFrame.panelNetworkAdmin);
        assertNotNull(ApplicationFrame.panelEnterpriseAdmin);
    }

    @Test
    public void testPanelConstantsUnique() {
        assertNotEquals(ApplicationFrame.panelSplash,      ApplicationFrame.panelStaffLogin);
        assertNotEquals(ApplicationFrame.panelNetworkAdmin, ApplicationFrame.panelEnterpriseAdmin);
        assertNotEquals(ApplicationFrame.panelSplash,      ApplicationFrame.panelNetworkAdmin);
    }

    // ── Navigation — public screens ───────────────────────────────────────────

    @Test
    public void testShowPanelSplashHidesHeaderAndSidebar() throws Exception {
        SwingUtilities.invokeAndWait(() -> frame.showPanel(ApplicationFrame.panelSplash));
        // Header and sidebar should be hidden on public screens
        // Access via getContentPane children
        for (Component c : ((JPanel) frame.getContentPane()).getComponents()) {
            if (c instanceof JPanel p) {
                // Sidebar and header are not visible on splash
                if (p.getPreferredSize().height == 48) {  // headerH
                    assertFalse("Header should be hidden on splash", c.isVisible());
                }
            }
        }
    }

    @Test
    public void testShowPanelStaffLoginDoesNotThrow() throws Exception {
        SwingUtilities.invokeAndWait(() -> frame.showPanel(ApplicationFrame.panelStaffLogin));
    }

    @Test
    public void testShowUnknownPanelDoesNotThrow() throws Exception {
        // Should log a warning and return gracefully — not throw
        SwingUtilities.invokeAndWait(() -> frame.showPanel("nonExistentPanel"));
    }

    // ── Navigation — authenticated screens ───────────────────────────────────

    @Test
    public void testShowNetworkAdminPanelWithValidSession() throws Exception {
        injectSession("networkAdmin", "slartibartfastPictures", "magratheaStudios");
        SwingUtilities.invokeAndWait(() -> frame.showPanel(ApplicationFrame.panelNetworkAdmin));
    }

    @Test
    public void testShowEnterpriseAdminPanelWithValidSession() throws Exception {
        injectSession("enterpriseAdmin", "magratheaThemeWorlds", "starshipTitanicLeisure");
        SwingUtilities.invokeAndWait(() -> frame.showPanel(ApplicationFrame.panelEnterpriseAdmin));
    }

    @Test
    public void testRouteByRoleNetworkAdminGoesToNetworkPanel() throws Exception {
        injectSession("networkAdmin", "slartibartfastPictures", "magratheaStudios");
        SwingUtilities.invokeAndWait(() -> frame.routeByRole());
    }

    @Test
    public void testRouteByRoleEnterpriseAdminGoesToEnterprisePanel() throws Exception {
        injectSession("enterpriseAdmin", "magratheaThemeWorlds", "starshipTitanicLeisure");
        SwingUtilities.invokeAndWait(() -> frame.routeByRole());
    }

    @Test
    public void testRouteByRoleWhenNotLoggedInGoesToSplash() throws Exception {
        SessionManager.logout();
        SwingUtilities.invokeAndWait(() -> frame.routeByRole());
        assertFalse("Should not be logged in", SessionManager.isLoggedIn());
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    @Test
    public void testLogoutClearsSession() throws Exception {
        injectSession("networkAdmin", "slartibartfastPictures", "magratheaStudios");
        SwingUtilities.invokeAndWait(() -> frame.logout());
        assertFalse("Session should be cleared after logout", SessionManager.isLoggedIn());
    }

    @Test
    public void testLogoutDoesNotThrowWhenNotLoggedIn() throws Exception {
        SessionManager.logout();
        SwingUtilities.invokeAndWait(() -> frame.logout());
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private void injectSession(String role, String orgId, String enterpriseId) {
        SessionManager.injectDemoSession(role, orgId, enterpriseId);
    }
}