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

public class SidebarPanel extends ImageBackgroundPanel {

    private static final int sidebarW = 250;
    private static final int sectionPadTop = 20;
    private static final int itemH = 38;
    private static final Color sectionOverlay = new Color(8, 8, 20, 180);
    private static final Color sectionColor = new Color(85, 85, 110);
    private static final Color itemColor = new Color(144, 144, 192);
    private static final Color itemHover = new Color(200, 184, 248);
    private static final Color activeBg = new Color(26, 26, 58, 200);
    private static final Color borderColor = new Color(42, 42, 90);

    private final ApplicationFrame frame;
    private String activePanel = "";

    private record NavItem(String label, String panelName) {

    }

    private record NavSection(String title, List<NavItem> items) {

    }

    public SidebarPanel(ApplicationFrame frame) {
        super(ThemeService.getInstance().getPublicImage(),
                ImageBackgroundPanel.Treatment.FADE_RIGHT, 0.0f, Color.BLACK);
        this.frame = frame;
        setPreferredSize(new Dimension(sidebarW, 0));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    public void refresh() {
        removeAll();

        if (!SessionManager.isLoggedIn()) {
            revalidate();
            repaint();
            return;
        }

        String enterpriseId = SessionManager.getEnterpriseId();
        Color accentColor = ThemeService.getEnterpriseAccent(
                enterpriseId != null ? enterpriseId : "");

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

        add(javax.swing.Box.createVerticalGlue());

        revalidate();
        repaint();
    }

    public void setActivePanel(String panelName) {
        this.activePanel = panelName != null ? panelName : "";
        repaint();
    }

    private List<NavSection> buildNavForRole(String role) {
        if (role == null) {
            return List.of();
        }

        List<NavSection> sections = new ArrayList<>();

        if (Claims.roleGuest.equals(role)) {
            List<NavItem> guest = new ArrayList<>();
            guest.add(new NavItem("Bookings", ApplicationFrame.panelGuestBookings));
            guest.add(new NavItem("Complaints", ApplicationFrame.panelGuestComplaints));
            guest.add(new NavItem("Casino", ApplicationFrame.panelGuestCasino));
            sections.add(new NavSection("Guest", guest));
            return sections;
            
        }

        if (Claims.roleComplianceOfficer.equals(role)) {
            List<NavItem> workspace = new ArrayList<>();
            workspace.add(new NavItem("Compliance Dashboard", ApplicationFrame.panelComplianceOfficer));
            workspace.add(new NavItem("Reports", ApplicationFrame.panelReporting));
            sections.add(new NavSection("Workspace", workspace));

            List<NavItem> casino = new ArrayList<>();
            casino.add(new NavItem("Casino", "casino"));
            sections.add(new NavSection("Casino", casino));

            return sections;
        }

        List<NavItem> workspace = new ArrayList<>();
        workspace.add(new NavItem("Dashboard", resolveDashboardPanel(role)));
        workspace.add(new NavItem("Work Requests", ApplicationFrame.panelWorkRequests));

        if (Claims.roleOrgDirector.equals(role)
                || isEnterpriseLevel(role)
                || isNetworkLevel(role)) {
            workspace.add(new NavItem("My Organization", ApplicationFrame.panelOrgDirector));
        }
        sections.add(new NavSection("Workspace", workspace));

        List<NavItem> tools = new ArrayList<>();
        if (SessionManager.canAccessReports()) {
            tools.add(new NavItem("Reports", ApplicationFrame.panelReporting));
        }
        tools.add(new NavItem("H2G2 Guide", ApplicationFrame.panelAiGuide));
        if (!tools.isEmpty()) {
            sections.add(new NavSection("Tools", tools));
        }

        List<NavItem> admin = new ArrayList<>();
        if (isEnterpriseLevel(role) || isNetworkLevel(role)) {
            admin.add(new NavItem("Enterprise View", ApplicationFrame.panelEnterpriseAdmin));
        }
        if (isNetworkLevel(role)) {
            admin.add(new NavItem("Network View", ApplicationFrame.panelNetworkAdmin));
        }

        if (Claims.roleGroupCfo.equals(role)) {
            admin.add(new NavItem("CFO Dashboard", ApplicationFrame.panelCfo));
        }

        if (!admin.isEmpty()) {
            sections.add(new NavSection("Admin", admin));
        }

        List<NavItem> casino = new ArrayList<>();
        casino.add(new NavItem("Casino", "casino"));
        sections.add(new NavSection("Casino", casino));

        return sections;
    }

    private String resolveDashboardPanel(String role) {
        return switch (role) {
            case Claims.roleNetworkAdmin, Claims.roleSystemAdmin, Claims.roleGroupCeo ->
                ApplicationFrame.panelNetworkAdmin;

            case Claims.roleEnterpriseAdmin, Claims.roleEntPresident, Claims.roleEntCoo ->
                ApplicationFrame.panelEnterpriseAdmin;

            case Claims.roleOrgDirector ->
                ApplicationFrame.panelOrgDirector;

            case Claims.roleCreativeLead ->
                ApplicationFrame.panelCreativeLead;

            case Claims.roleTechnologyLead ->
                ApplicationFrame.panelTechnologyLead;

            case Claims.roleMarketingLead ->
                ApplicationFrame.panelMarketingLead;

            case Claims.roleComplianceOfficer ->
                ApplicationFrame.panelComplianceOfficer;

            case Claims.roleDataAnalyst ->
                ApplicationFrame.panelDataAnalyst;

            case Claims.roleGroupCfo ->
                ApplicationFrame.panelCfo;

            case Claims.roleGuest ->
                ApplicationFrame.panelGuestCasino;

            default ->
                ApplicationFrame.panelSplash;
        };
    }

    private boolean isNetworkLevel(String role) {
        return Claims.roleNetworkAdmin.equals(role)
                || Claims.roleSystemAdmin.equals(role)
                || Claims.roleGroupCeo.equals(role);
    }

    private boolean isEnterpriseLevel(String role) {
        return Claims.roleEnterpriseAdmin.equals(role)
                || Claims.roleEntPresident.equals(role)
                || Claims.roleEntCoo.equals(role);
    }

    private JPanel buildSectionLabel(String title) {
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(sectionOverlay);
                g2.fillRect(0, sectionPadTop - 2,
                        getWidth(), getHeight() - sectionPadTop + 2);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(sidebarW, sectionPadTop + 24));
        panel.setPreferredSize(new Dimension(sidebarW, sectionPadTop + 24));

        JLabel label = new JLabel(title.toUpperCase());
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        label.setForeground(new Color(180, 170, 210));
        label.setBounds(14, sectionPadTop, sidebarW - 28, 20);
        panel.add(label);

        return panel;
    }

    private JPanel buildNavItem(NavItem item, Color accentColor) {
        boolean isActive = item.panelName().equals(activePanel);

        JPanel row = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                if (item.panelName().equals(activePanel)) {
                    g2.setColor(activeBg);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(accentColor);
                    g2.fillRect(0, 0, 3, getHeight());
                } else {
                    g2.setColor(new Color(8, 8, 20, 210));
                    g2.fillRoundRect(8, 6, getWidth() - 16, getHeight() - 12, 8, 8);
                }
                g2.dispose();
            }
        };

        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(sidebarW, itemH));
        row.setPreferredSize(new Dimension(sidebarW, itemH));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel nameLabel = new JLabel(item.label());
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        nameLabel.setForeground(isActive ? ThemeService.colorTextPrimary : itemColor);
        nameLabel.setBounds(14, 0, sidebarW - 28, itemH);
        row.add(nameLabel);

        boolean isGuest = SessionManager.guard(Claims.roleGuest);
        boolean isReports = item.label().equalsIgnoreCase("Reports");

        if (isGuest && isReports) {
            row.setCursor(Cursor.getDefaultCursor());
            nameLabel.setForeground(new Color(120, 120, 120));
            row.setToolTipText("Guests cannot access Reports");

            row.addMouseListener(new MouseAdapter() {
            });
        } else {
            row.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!item.panelName().equals(activePanel)) {
                        nameLabel.setForeground(itemHover);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    nameLabel.setForeground(
                            item.panelName().equals(activePanel)
                            ? ThemeService.colorTextPrimary : itemColor);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    frame.showPanel(item.panelName());
                }
            });
        }

        return row;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(borderColor);
        g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
        g2.dispose();
    }
}
