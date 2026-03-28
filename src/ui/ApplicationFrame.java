

package ui;

import model.Claims;
import service.ConfigService;
import service.SessionManager;
import service.ThemeService;
import ui.panels.HeaderPanel;
import ui.panels.SplashPanel;
import ui.panels.StaffLoginPanel;

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

    private static final Logger log = Logger.getLogger(ApplicationFrame.class.getName());

    // -------------------------------------------------------------------------
    // Panel name constants — use these everywhere, never hardcode strings
    // -------------------------------------------------------------------------
    public static final String panelSplash             = "splash";
    public static final String panelStaffLogin         = "staffLogin";
    public static final String panelGuestLogin         = "guestLogin";
    public static final String panelGuestRegister      = "guestRegister";
    public static final String panelNetworkAdmin       = "networkAdmin";
    public static final String panelEnterpriseAdmin    = "enterpriseAdmin";
    public static final String panelOrgDirector        = "orgDirector";
    public static final String panelWorkRequests       = "workRequests";
    public static final String panelCreativeLead       = "creativeLead";
    public static final String panelTechnologyLead     = "technologyLead";
    public static final String panelMarketingLead      = "marketingLead";
    public static final String panelComplianceOfficer  = "complianceOfficer";
    public static final String panelDataAnalyst        = "dataAnalyst";
    public static final String panelReporting          = "reporting";
    public static final String panelGuestPortal        = "guestPortal";
    public static final String panelGuestBookings      = "guestBookings";
    public static final String panelGuestCasino        = "guestCasino";
    public static final String panelGuestComplaints    = "guestComplaints";
    public static final String panelAiGuide            = "aiGuide";
    public static final String panelMap                = "map";

    // -------------------------------------------------------------------------
    // Layout components
    // -------------------------------------------------------------------------
    private final CardLayout  cardLayout    = new CardLayout();
    private final JPanel      cardContainer = new JPanel(cardLayout);
    private       HeaderPanel headerPanel;
    private       JPanel      sidebarPanel;

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
        showPanel(panelSplash);
    }

    // -------------------------------------------------------------------------
    // Initialisation
    // -------------------------------------------------------------------------

    private void initWindow() {
        ConfigService cfg = ConfigService.getInstance();
        int mw = cfg.getInt("app.window.min.width",  1024);
        int mh = cfg.getInt("app.window.min.height", 680);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(mw, mh));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setBackground(ThemeService.colorBgPrimary);

        try {
            ImageIcon icon = new ImageIcon(
                getClass().getResource("/resources/images/deep_field.png"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            log.fine("App icon not found — skipping");
        }
    }

    private void initPanels() {
        // Public panels — no session required
        register(panelSplash,      new SplashPanel(this));
        register(panelStaffLogin,  new StaffLoginPanel(this));
//        register(panelGuestLogin,     new GuestLoginPanel(this));
//        register(panelGuestRegister,  new GuestRegistrationPanel(this));

        // Staff panels — session + role required
//        register(panelNetworkAdmin,      new NetworkAdminPanel(this));
//        register(panelEnterpriseAdmin,   new EnterpriseAdminPanel(this));
//        register(panelOrgDirector,       new OrgDirectorPanel(this));
//        register(panelWorkRequests,      new WorkRequestPanel(this));
//        register(panelCreativeLead,      new CreativeLeadPanel(this));
//        register(panelTechnologyLead,    new TechnologyLeadPanel(this));
//        register(panelMarketingLead,     new MarketingLeadPanel(this));
//        register(panelComplianceOfficer, new ComplianceOfficerPanel(this));
//        register(panelDataAnalyst,       new DataAnalystPanel(this));
//        register(panelReporting,         new ReportingPanel(this));
//        register(panelAiGuide,           new AIGuidePanel(this));

        // Guest panels — guest session required
//        register(panelGuestPortal,     new GuestPortalPanel(this));
//        register(panelGuestBookings,   new GuestBookingsPanel(this));
//        register(panelGuestCasino,     new GuestCasinoPanel(this));
//        register(panelGuestComplaints, new GuestComplaintsPanel(this));
//        register(panelMap,             new MapPanel(this));
    }

    private void initLayout() {
        headerPanel  = new HeaderPanel(this);
        sidebarPanel = new JPanel();
        sidebarPanel.setBackground(ThemeService.colorBgPrimary);
        sidebarPanel.setPreferredSize(new Dimension(180, getHeight()));

        headerPanel.setVisible(false);
        sidebarPanel.setVisible(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(ThemeService.colorBgPrimary);
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
     * @param name one of the panel* constants defined above
     */
    public void showPanel(String name) {
        if (!panels.containsKey(name)) {
            log.warning("Unknown panel: " + name);
            return;
        }

        boolean isPublicScreen = name.equals(panelSplash)
            || name.equals(panelStaffLogin)
            || name.equals(panelGuestLogin)
            || name.equals(panelGuestRegister);

        headerPanel.setVisible(!isPublicScreen);
        sidebarPanel.setVisible(!isPublicScreen);

        if (!isPublicScreen && SessionManager.isLoggedIn()) {
            headerPanel.refresh();
            sidebarPanel.repaint();
        }

        cardLayout.show(cardContainer, name);
        log.fine("Navigated to panel: " + name);
    }

    /**
     * Routes the user to their home panel based on their JWT role claim.
     * Call this immediately after a successful login.
     */
    public void routeByRole() {
        if (!SessionManager.isLoggedIn()) {
            showPanel(panelSplash);
            return;
        }

        String role = SessionManager.getRole();

        String target = switch (role) {
            case Claims.roleNetworkAdmin,
                 Claims.roleSystemAdmin,
                 Claims.roleGroupCeo,
                 Claims.roleGroupCfo        -> panelNetworkAdmin;
            case Claims.roleEnterpriseAdmin,
                 Claims.roleEntPresident,
                 Claims.roleEntCoo          -> panelEnterpriseAdmin;
            case Claims.roleOrgDirector     -> panelOrgDirector;
            case Claims.roleCreativeLead    -> panelCreativeLead;
            case Claims.roleTechnologyLead  -> panelTechnologyLead;
            case Claims.roleMarketingLead   -> panelMarketingLead;
            case Claims.roleComplianceOfficer -> panelComplianceOfficer;
            case Claims.roleDataAnalyst     -> panelDataAnalyst;
            case Claims.roleGuest           -> panelGuestPortal;
            default -> {
                log.warning("Unrecognised role: " + role + " — routing to splash");
                yield panelSplash;
            }
        };

        showPanel(target);
        headerPanel.refresh();
        log.info("Routed role " + role + " to panel: " + target);
    }

    /**
     * Logs out the current user, clears the session, and returns to splash.
     */
    public void logout() {
        SessionManager.logout();
        headerPanel.setVisible(false);
        sidebarPanel.setVisible(false);
        showPanel(panelSplash);
        log.info("User logged out");
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
                log.fine("Could not set system look and feel");
            }
            new ApplicationFrame().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
