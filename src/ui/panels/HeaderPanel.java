package ui.panels;

import service.SessionManager;
import service.ThemeService;
import ui.ApplicationFrame;
import ui.components.ImageBackgroundPanel;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Application header — shown on all authenticated screens.
 * JWST accent strip background via ImageBackgroundPanel.STRIP treatment.
 *
 * Layout (left → right):
 *   [star icon + network name]   [user · role · org]   [log out button]
 *
 * Call refresh() after login and after any session change so the
 * user info label reflects the current session state.
 */
public class HeaderPanel extends ImageBackgroundPanel {

    // ── Layout constants ──────────────────────────────────────────────────────
    private static final int   headerH    = 48;
    private static final int   sidePad    = 24;
    private static final int   rightPad   = 32;
    private static final Color  borderColor    = new Color(42, 42, 90);
    private static final Color  btnBg          = new Color(42, 42, 90);
    private static final Color  btnBgHover     = new Color(60, 60, 120);
    private static final Color  btnFg          = new Color(160, 144, 224);
    private static final Color  btnBorder      = new Color(58, 58, 122);

    // ── Services / frame ─────────────────────────────────────────────────────
    private final ApplicationFrame frame;

    // ── UI components ─────────────────────────────────────────────────────────
    private JLabel logoLabel;
    private JLabel userInfoLabel;
    private JButton logoutButton;

    // ─────────────────────────────────────────────────────────────────────────

    public HeaderPanel(ApplicationFrame frame) {
        super(ThemeService.getInstance().getPublicImage(),
              ImageBackgroundPanel.Treatment.STRIP, 0.0f, Color.BLACK);
        this.frame = frame;
        setLayout(null);
        setPreferredSize(new java.awt.Dimension(0, headerH));
        initComponents();
    }

    // ── Component init ────────────────────────────────────────────────────────

    private void initComponents() {
        // ── Logo / network name ───────────────────────────────────────────
        logoLabel = new JLabel("\u2B50  Deep Thought Entertainment Group");
        logoLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        logoLabel.setForeground(ThemeService.colorTextPrimary);
        logoLabel.setOpaque(false);
        add(logoLabel);

        // ── User info — name · role · org ─────────────────────────────────
        userInfoLabel = new JLabel("");
        userInfoLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        userInfoLabel.setForeground(ThemeService.colorTextSecondary);
        userInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        userInfoLabel.setOpaque(false);
        add(userInfoLabel);

        // ── Logout button ─────────────────────────────────────────────────
        logoutButton = new JButton("Log out") {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? btnBgHover : btnBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(btnBorder);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoutButton.setForeground(btnFg);
        logoutButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        logoutButton.setOpaque(false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> frame.logout());
        add(logoutButton);
    }

    // ── Layout ────────────────────────────────────────────────────────────────

    @Override
    public void doLayout() {
        super.doLayout();
        int w = getWidth();
        int mid = w / 2;

        // Logo — left-anchored
        logoLabel.setBounds(sidePad, 0, 340, headerH);

        // Logout button — right-anchored
        int btnW = 100;
        int btnH = 26;
        int btnX = w - rightPad - btnW;
        int btnY = (headerH - btnH) / 2;
        logoutButton.setBounds(btnX, btnY, btnW, btnH);

        // User info — centered between logo right edge and button left edge
        int infoX = sidePad + 340 + 8;
        int infoW = btnX - infoX - 8;
        userInfoLabel.setBounds(infoX, 0, infoW, headerH);
    }

    // ── Paint ─────────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // STRIP treatment paints JWST accent

        // Bottom border line
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(borderColor);
        g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
        g2.dispose();
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Refreshes the user info label from the current session.
     * Call after login, role switch, or demo session injection.
     */
    public void refresh() {
        if (!SessionManager.isLoggedIn()) {
            userInfoLabel.setText("");
            return;
        }

        String userId = SessionManager.getUserId();
        String role   = formatRole(SessionManager.getRole());
        String org    = formatOrg(SessionManager.getOrgId());

        StringBuilder info = new StringBuilder();
        if (userId != null && !userId.isBlank()) info.append(userId);
        if (role   != null && !role.isBlank())   info.append("  \u00B7  ").append(role);
        if (org    != null && !org.isBlank())     info.append("  \u00B7  ").append(org);

        userInfoLabel.setText(info.toString());

        // Update JWST image to match the user's enterprise
        String enterpriseId = SessionManager.getEnterpriseId();
        if (enterpriseId != null && !enterpriseId.isBlank()) {
            setBackgroundImage(ThemeService.getInstance().getEnterpriseImage(enterpriseId));
        }

        repaint();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Converts camelCase role constant to a readable display string.
     * e.g. "orgDirector" -> "Org Director"
     */
    private static String formatRole(String role) {
        if (role == null || role.isBlank()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < role.length(); i++) {
            char c = role.charAt(i);
            if (Character.isUpperCase(c) && i > 0) sb.append(' ');
            sb.append(i == 0 ? Character.toUpperCase(c) : c);
        }
        return sb.toString();
    }

    /**
     * Converts camelCase org ID to a readable display string.
     * e.g. "slartibartfastPictures" -> "Slartibartfast Pictures"
     */
    private static String formatOrg(String org) {
        return formatRole(org); // same camelCase splitting logic
    }
}