package ui.panels;

import model.Claims;
import service.SessionManager;
import service.ThemeService;
import ui.ApplicationFrame;
import ui.components.ImageBackgroundPanel;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Role-aware navigation sidebar — shown on all authenticated screens.
 * JWST fade-right background via ImageBackgroundPanel.FADE_RIGHT treatment.
 *
 * Navigation items are built from the current session's role on refresh().
 * Only items the role is permitted to access are shown — no dimmed items.
 * Active item is highlighted with the enterprise accent color.
 *
 * Call refresh() after login and after any session or panel change.
 * Call setActivePanel(String panelName) to update the active highlight.
 */
public class SidebarPanel extends ImageBackgroundPanel {

    // ── Layout constants ──────────────────────────────────────────────────────
    private static final int   sidebarW      = 200;
    private static final int   sectionPadTop = 20;
    private static final int   itemH         = 38;
    private static final Color sectionColor  = new Color(85, 85, 110);
    private static final Color itemColor     = new Color(144, 144, 192);
    private static final Color itemHover     = new Color(200, 184, 248);
    private static final Color activeBg      = new Color(26, 26, 58);
    private static final Color borderColor   = new Color(42, 42, 90);

    // ── Services / frame ─────────────────────────────────────────────────────
    private final ApplicationFrame frame;

    // ── State ─────────────────────────────────────────────────────────────────
    private String activePanel = "";

    // ── Nav item model ────────────────────────────────────────────────────────
    private record NavItem(String label, String panelName) {}
    private record NavSection(String title, List<NavItem> items) {}

    // ─────────────────────────────────────────────────────────────────────────

    public SidebarPanel(ApplicationFrame frame) {
        super(ThemeService.getInstance().getPublicImage(),
              ImageBackgroundPanel.Treatment.FADE_RIGHT, 0.0f, Color.BLACK);
        this.frame = frame;
        setPreferredSize(new Dimension(sidebarW, 0));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Rebuilds the sidebar nav from the current session.
     * Call after login, role switch, or demo session injection.
     */
    public void refresh() {
        removeAll();

        if (!SessionManager.isLoggedIn()) {
            revalidate();
            repaint();
            return;
        }

        String enterpriseId = SessionManager.getEnterpriseId();
        Color  accentColor  = ThemeService.getEnterpriseAccent(
            enterpriseId != null ? enterpriseId : "");

        // Update JWST image to match enterprise
        if (enterpriseId != null && !enterpriseId.isBlank()) {
            setBackgroundImage(ThemeService.getInstance().getEnterpriseImage(enterpriseId));
        }

        List<NavSection> sections = buildNavForRole(SessionManager.getRole());
        for (NavSection section : sections) {
            add(buildSectionLabel(section.title()));
            for (NavItem item : section.items()) {
                add(buildNavItem(item, accentColor));
            }
        }

        // Push remaining space to bottom
        add(javax.swing.Box.createVerticalGlue());

        revalidate();
        repaint();
    }

    /**
     * Updates the active highlight to the given panel name.
     * Call from showPanel() whenever navigation occurs.
     */
    public void setActivePanel(String panelName) {
        this.activePanel = panelName != null ? panelName : "";
        repaint();
    }

    // ── Nav structure by role ─────────────────────────────────────────────────

    private List<NavSection> buildNavForRole(String role) {
        if (role == null) return List.of();

        List<NavSection> sections = new ArrayList<>();

        // ── Workspace section — visible to all staff ──────────────────────
        List<NavItem> workspace = new ArrayList<>();
        workspace.add(new NavItem("Dashboard",     resolveDashboardPanel(role)));
        workspace.add(new NavItem("Work Requests", ApplicationFrame.panelWorkRequests));

        if (Claims.roleOrgDirector.equals(role)
                || isEnterpriseLevel(role)
                || isNetworkLevel(role)) {
            workspace.add(new NavItem("My Organization", ApplicationFrame.panelOrgDirector));
        }
        sections.add(new NavSection("Workspace", workspace));

        // ── Tools section ─────────────────────────────────────────────────
        List<NavItem> tools = new ArrayList<>();
        if (SessionManager.canAccessReports()) {
            tools.add(new NavItem("Reports", ApplicationFrame.panelReporting));
        }
        tools.add(new NavItem("H2G2 Guide", ApplicationFrame.panelAiGuide));
        if (!tools.isEmpty()) sections.add(new NavSection("Tools", tools));

        // ── Admin section — enterprise and network levels only ────────────
        List<NavItem> admin = new ArrayList<>();
        if (isEnterpriseLevel(role) || isNetworkLevel(role)) {
            admin.add(new NavItem("Enterprise View", ApplicationFrame.panelEnterpriseAdmin));
        }
        if (isNetworkLevel(role)) {
            admin.add(new NavItem("Network View", ApplicationFrame.panelNetworkAdmin));
        }
        if (!admin.isEmpty()) sections.add(new NavSection("Admin", admin));

        return sections;
    }

    /** Returns the home dashboard panel for the given role. */
    private String resolveDashboardPanel(String role) {
        return switch (role) {
            case Claims.roleNetworkAdmin,
                 Claims.roleSystemAdmin,
                 Claims.roleGroupCeo,
                 Claims.roleGroupCfo        -> ApplicationFrame.panelNetworkAdmin;
            case Claims.roleEnterpriseAdmin,
                 Claims.roleEntPresident,
                 Claims.roleEntCoo          -> ApplicationFrame.panelEnterpriseAdmin;
            case Claims.roleOrgDirector     -> ApplicationFrame.panelOrgDirector;
            case Claims.roleCreativeLead    -> ApplicationFrame.panelCreativeLead;
            case Claims.roleTechnologyLead  -> ApplicationFrame.panelTechnologyLead;
            case Claims.roleMarketingLead   -> ApplicationFrame.panelMarketingLead;
            case Claims.roleComplianceOfficer -> ApplicationFrame.panelComplianceOfficer;
            case Claims.roleDataAnalyst     -> ApplicationFrame.panelDataAnalyst;
            default                         -> ApplicationFrame.panelSplash;
        };
    }

    private boolean isNetworkLevel(String role) {
        return Claims.roleNetworkAdmin.equals(role)
            || Claims.roleSystemAdmin.equals(role)
            || Claims.roleGroupCeo.equals(role)
            || Claims.roleGroupCfo.equals(role);
    }

    private boolean isEnterpriseLevel(String role) {
        return Claims.roleEnterpriseAdmin.equals(role)
            || Claims.roleEntPresident.equals(role)
            || Claims.roleEntCoo.equals(role);
    }

    // ── Component builders ────────────────────────────────────────────────────

    private JLabel buildSectionLabel(String title) {
        JLabel label = new JLabel(title.toUpperCase());
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        label.setForeground(sectionColor);
        label.setMaximumSize(new Dimension(sidebarW, 28));
        label.setPreferredSize(new Dimension(sidebarW, 28));
        label.setBorder(javax.swing.BorderFactory.createEmptyBorder(
            sectionPadTop, 14, 4, 14));
        label.setOpaque(false);
        return label;
    }

    private JPanel buildNavItem(NavItem item, Color accentColor) {
        boolean isActive = item.panelName().equals(activePanel);

        JPanel row = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (item.panelName().equals(activePanel)) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(activeBg);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(accentColor);
                    g2.fillRect(0, 0, 3, getHeight());
                    g2.dispose();
                }
            }
        };
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(sidebarW, itemH));
        row.setPreferredSize(new Dimension(sidebarW, itemH));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel nameLabel = new JLabel(item.label());
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        nameLabel.setForeground(isActive ? ThemeService.colorTextPrimary : itemColor);
        nameLabel.setBounds(14, 0, sidebarW - 14, itemH);
        row.add(nameLabel);

        row.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (!item.panelName().equals(activePanel)) {
                    nameLabel.setForeground(itemHover);
                }
            }
            @Override public void mouseExited(MouseEvent e) {
                nameLabel.setForeground(
                    item.panelName().equals(activePanel)
                        ? ThemeService.colorTextPrimary : itemColor);
            }
            @Override public void mouseClicked(MouseEvent e) {
                frame.showPanel(item.panelName());
            }
        });

        return row;
    }

    // ── Paint ─────────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // FADE_RIGHT treatment

        // Right border line
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(borderColor);
        g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
        g2.dispose();
    }
}