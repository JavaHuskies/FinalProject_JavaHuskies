
package ui;

import model.Claims;
import service.ConfigService;
import service.SessionManager;
import service.ThemeService;
import ui.components.ImageBackgroundPanel;
import ui.panels.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Top-level JFrame — the application window.
 * Owns the CardLayout container and all registered panels.
 * Handles role-based routing after login and demo mode switching.
 *
 * All panels receive a reference to this frame in their constructor
 * so they can trigger navigation via frame.showPanel(String name).
 *
 * Panel name constants are defined as static strings below —
 * always use these rather than hardcoded strings.
 */
public class ApplicationFrame extends JFrame {

    private static final Logger LOG = Logger.getLogger(ApplicationFrame.class.getName());

    // -------------------------------------------------------------------------
    // Panel name constants — use these everywhere, never hardcode strings
    // -------------------------------------------------------------------------
    public static final String PANEL_SPLASH              = "splash";
    public static final String PANEL_STAFF_LOGIN         = "staffLogin";
    public static final String PANEL_GUEST_LOGIN         = "guestLogin";
    public static final String PANEL_GUEST_REGISTER      = "guestRegister";
    public static final String PANEL_NETWORK_ADMIN       = "networkAdmin";
    public static final String PANEL_ENTERPRISE_ADMIN    = "enterpriseAdmin";
    public static final String PANEL_ORG_DIRECTOR        = "orgDirector";
    public static final String PANEL_WORK_REQUESTS       = "workRequests";
    public static final String PANEL_CREATIVE_LEAD       = "creativeLead";
    public static final String PANEL_TECHNOLOGY_LEAD     = "technologyLead";
    public static final String PANEL_MARKETING_LEAD      = "marketingLead";
    public static final String PANEL_COMPLIANCE_OFFICER  = "complianceOfficer";
    public static final String PANEL_DATA_ANALYST        = "dataAnalyst";
    public static final String PANEL_REPORTING           = "reporting";
    public static final String PANEL_GUEST_PORTAL        = "guestPortal";
    public static final String PANEL_GUEST_BOOKINGS      = "guestBookings";
    public static final String PANEL_GUEST_CASINO        = "guestCasino";
    public static final String PANEL_GUEST_COMPLAINTS    = "guestComplaints";
    public static final String PANEL_AI_GUIDE            = "aiGuide";
    public static final String PANEL_MAP                 = "map";

    // -------------------------------------------------------------------------
    // Layout components
    // -------------------------------------------------------------------------
    private final CardLayout cardLayout    = new CardLayout();
    private final JPanel     cardContainer = new JPanel(cardLayout);
    private       JPanel     headerPanel;
    private       JPanel     sidebarPanel;

    // Registered panels map — name -> panel
    private final Map<String, JPanel> panels = new HashMap<>();

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public ApplicationFrame() {
        super(ConfigService.getInstance().get("app.name",
              "Deep Thought Entertainment Group"));
        initWindow();
        initPanels();
        initLayout();
        initDemoModeShortcut();
        showPanel(PANEL_SPLASH);
    }

    // -------------------------------------------------------------------------
    // Initialisation
    // -------------------------------------------------------------------------

    private void initWindow() {
        ConfigService cfg = ConfigService.getInstance();
        int w  = cfg.getInt("app.window.width",      1280);
        int h  = cfg.getInt("app.window.height",     800);
        int mw = cfg.getInt("app.window.min.width",  1024);
        int mh = cfg.getInt("app.window.min.height", 680);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(w, h);
        setMinimumSize(new Dimension(mw, mh));
        setLocationRelativeTo(null);
        setBackground(ThemeService.COLOR_BG_PRIMARY);

        try {
            ImageIcon icon = new ImageIcon(
                getClass().getResource("/resources/images/deep_field.jpg"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            LOG.fine("App icon not found — skipping");
        }
    }

    private void initPanels() {
        // Public panels — no session required
        register(PANEL_SPLASH,           new SplashPanel(this));
        register(PANEL_STAFF_LOGIN,      new StaffLoginPanel(this));
//        register(PANEL_GUEST_LOGIN,      new GuestLoginPanel(this));
//        register(PANEL_GUEST_REGISTER,   new GuestRegistrationPanel(this));

        // Staff panels — session + role required
//        register(PANEL_NETWORK_ADMIN,      new NetworkAdminPanel(this));
//        register(PANEL_ENTERPRISE_ADMIN,   new EnterpriseAdminPanel(this));
//        register(PANEL_ORG_DIRECTOR,       new OrgDirectorPanel(this));
//        register(PANEL_WORK_REQUESTS,      new WorkRequestPanel(this));
//        register(PANEL_CREATIVE_LEAD,      new CreativeLeadPanel(this));
//        register(PANEL_TECHNOLOGY_LEAD,    new TechnologyLeadPanel(this));
//        register(PANEL_MARKETING_LEAD,     new MarketingLeadPanel(this));
//        register(PANEL_COMPLIANCE_OFFICER, new ComplianceOfficerPanel(this));
//        register(PANEL_DATA_ANALYST,       new DataAnalystPanel(this));
//        register(PANEL_REPORTING,          new ReportingPanel(this));
//        register(PANEL_AI_GUIDE,           new AIGuidePanel(this));

        // Guest panels — guest session required
//        register(PANEL_GUEST_PORTAL,     new GuestPortalPanel(this));
//        register(PANEL_GUEST_BOOKINGS,   new GuestBookingsPanel(this));
//        register(PANEL_GUEST_CASINO,     new GuestCasinoPanel(this));
//        register(PANEL_GUEST_COMPLAINTS, new GuestComplaintsPanel(this));
//        register(PANEL_MAP,              new MapPanel(this));
    }

    private void initLayout() {
        headerPanel  = new JPanel();
        sidebarPanel = new JPanel();
        headerPanel.setBackground(ThemeService.COLOR_BG_SECONDARY);
        sidebarPanel.setBackground(ThemeService.COLOR_BG_PRIMARY);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 48));
        sidebarPanel.setPreferredSize(new Dimension(180, getHeight()));

        headerPanel.setVisible(false);
        sidebarPanel.setVisible(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(ThemeService.COLOR_BG_PRIMARY);
        root.add(headerPanel,   BorderLayout.NORTH);
        root.add(sidebarPanel,  BorderLayout.WEST);
        root.add(cardContainer, BorderLayout.CENTER);

        setContentPane(root);
    }

    private void register(String name, JPanel panel) {
        panels.put(name, panel);
        cardContainer.add(panel, name);
    }

    // -------------------------------------------------------------------------
    // Navigation
    // -------------------------------------------------------------------------

    /**
     * Shows the named panel and updates header/sidebar visibility.
     * This is the primary navigation method — panels call this to navigate.
     *
     * @param name one of the PANEL_* constants defined above
     */
    public void showPanel(String name) {
        if (!panels.containsKey(name)) {
            LOG.warning("Unknown panel: " + name);
            return;
        }

        boolean isPublicScreen = name.equals(PANEL_SPLASH)
            || name.equals(PANEL_STAFF_LOGIN)
            || name.equals(PANEL_GUEST_LOGIN)
            || name.equals(PANEL_GUEST_REGISTER);

        headerPanel.setVisible(!isPublicScreen);
        sidebarPanel.setVisible(!isPublicScreen);

        if (!isPublicScreen && SessionManager.isLoggedIn()) {
            headerPanel.repaint();
            sidebarPanel.repaint();
        }

        cardLayout.show(cardContainer, name);
        LOG.fine("Navigated to panel: " + name);
    }

    /**
     * Routes the user to their home panel based on their JWT role claim.
     * Call this immediately after a successful login.
     */
    public void routeByRole() {
        if (!SessionManager.isLoggedIn()) {
            showPanel(PANEL_SPLASH);
            return;
        }

        String role = SessionManager.getRole();

        String target = switch (role) {
            case Claims.roleNetworkAdmin,
                 Claims.roleSystemAdmin,
                 Claims.roleGroupCeo,
                 Claims.roleGroupCfo        -> PANEL_NETWORK_ADMIN;
            case Claims.roleEnterpriseAdmin,
                 Claims.roleEntPresident,
                 Claims.roleEntCoo          -> PANEL_ENTERPRISE_ADMIN;
            case Claims.roleOrgDirector     -> PANEL_ORG_DIRECTOR;
            case Claims.roleCreativeLead    -> PANEL_CREATIVE_LEAD;
            case Claims.roleTechnologyLead  -> PANEL_TECHNOLOGY_LEAD;
            case Claims.roleMarketingLead   -> PANEL_MARKETING_LEAD;
            case Claims.roleComplianceOfficer -> PANEL_COMPLIANCE_OFFICER;
            case Claims.roleDataAnalyst     -> PANEL_DATA_ANALYST;
            case Claims.roleGuest           -> PANEL_GUEST_PORTAL;
            default -> {
                LOG.warning("Unrecognised role: " + role + " — routing to splash");
                yield PANEL_SPLASH;
            }
        };

        showPanel(target);
        LOG.info("Routed role " + role + " to panel: " + target);
    }

    /**
     * Logs out the current user, clears the session, and returns to splash.
     */
    public void logout() {
        SessionManager.logout();
        headerPanel.setVisible(false);
        sidebarPanel.setVisible(false);
        showPanel(PANEL_SPLASH);
        LOG.info("User logged out");
    }

    // -------------------------------------------------------------------------
    // Demo mode — Ctrl+Shift+D
    // -------------------------------------------------------------------------

    private void initDemoModeShortcut() {
        if (!ConfigService.getInstance().getBool("app.demo.mode.enabled", false)) return;

        KeyboardFocusManager.getCurrentKeyboardFocusManager()
            .addKeyEventDispatcher(e -> {
                if (e.getID() == KeyEvent.KEY_PRESSED
                        && e.isControlDown()
                        && e.isShiftDown()
                        && e.getKeyCode() == KeyEvent.VK_D) {
                    showDemoSwitcher();
                    return true;
                }
                return false;
            });
    }

    private void showDemoSwitcher() {
        String[] roles = {
            Claims.roleNetworkAdmin,
            Claims.roleEnterpriseAdmin,
            Claims.roleEntPresident,
            Claims.roleEntCoo,
            Claims.roleOrgDirector,
            Claims.roleCreativeLead,
            Claims.roleTechnologyLead,
            Claims.roleMarketingLead,
            Claims.roleComplianceOfficer,
            Claims.roleDataAnalyst,
            Claims.roleGuest
        };

        String[] orgs = {
            "slartibartfastPictures",
            "bistromathAnimation",
            "magratheaThemeWorlds",
            "milliwaysEntertainment",
            "infiniteImprobabilityStreaming",
            "panGalacticBroadcast",
            "megadodoLicensing",
            "hooloovooRetail"
        };

        String[] enterprises = {
            "magratheaStudios",
            "starshipTitanicLeisure",
            "galacticBroadcasting",
            "siriusCybernetics"
        };

        JComboBox<String> roleBox       = new JComboBox<>(roles);
        JComboBox<String> orgBox        = new JComboBox<>(orgs);
        JComboBox<String> enterpriseBox = new JComboBox<>(enterprises);

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.add(new JLabel("Role:"));
        form.add(roleBox);
        form.add(new JLabel("Organization:"));
        form.add(orgBox);
        form.add(new JLabel("Enterprise:"));
        form.add(enterpriseBox);

        int result = JOptionPane.showConfirmDialog(
            this, form,
            "Demo mode — switch role (Ctrl+Shift+D)",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            SessionManager.injectDemoSession(
                (String) roleBox.getSelectedItem(),
                (String) orgBox.getSelectedItem(),
                (String) enterpriseBox.getSelectedItem()
            );
            routeByRole();
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Validate config before launching
        if (!ConfigService.getInstance().isLoaded()) {
            JOptionPane.showMessageDialog(null,
                "config.properties not found.\n" +
                "Copy config.properties.template, fill in your values,\n" +
                "and place it in the project root alongside src/ and lib/.",
                "Configuration Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                LOG.fine("Could not set system look and feel");
            }
            new ApplicationFrame().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
