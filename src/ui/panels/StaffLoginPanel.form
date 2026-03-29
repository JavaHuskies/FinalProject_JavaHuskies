package ui.panels;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import model.Claims;
import service.AuthService;
import service.SessionManager;
import service.ThemeService;
import ui.ApplicationFrame;
import ui.components.ImageBackgroundPanel;

/**
 * Staff login panel — hand-coded per project spec.
 * Extends ImageBackgroundPanel to inherit JWST background rendering.
 * Authenticates via AuthService (BCrypt verify + JWT issue/validate),
 * establishes session via SessionManager.login(), then routes via
 * ApplicationFrame.routeByRole().
 *
 * NOTE: Database lookup is stubbed until PersistenceService is live (Anan).
 * Swap the stub block in attemptLogin() for the real DB query when ready.
 */
public class StaffLoginPanel extends ImageBackgroundPanel {

    // ── Layout constants ──────────────────────────────────────────────────────
    private static final int   CARD_W       = 400;
    private static final int   CARD_H       = 420;
    private static final int   CARD_ARC     = 20;
    private static final Color CARD_BG      = new Color(10, 10, 26, 210);
    private static final Color CARD_BORDER  = new Color(80, 80, 140, 180);
    private static final Color ACCENT       = ThemeService.COLOR_ACCENT_PURPLE;
    private static final Color TEXT_PRIMARY = ThemeService.COLOR_TEXT_PRIMARY;
    private static final Color TEXT_MUTED   = ThemeService.COLOR_TEXT_MUTED;
    private static final Color FIELD_BG     = new Color(20, 20, 46, 230);
    private static final Color FIELD_BORDER = new Color(60, 60, 110, 200);
    private static final Color FIELD_FOCUS  = new Color(100, 120, 220, 180);
    private static final Color ERR_COLOR    = new Color(220, 80, 80);
    private static final Color BTN_NORMAL   = new Color(70, 90, 200);
    private static final Color BTN_HOVER    = new Color(90, 110, 230);
    private static final Color BTN_PRESS    = new Color(50, 70, 170);

    // ── Services / frame ─────────────────────────────────────────────────────
    private final ApplicationFrame frame;
    private final AuthService       auth;

    // ── UI components ─────────────────────────────────────────────────────────
    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JLabel         errorLabel;
    private JLabel         showHideLabel;
    private JButton        loginButton;
    private JLabel         guestLink;

    // ── State ─────────────────────────────────────────────────────────────────
    private boolean passwordVisible = false;
    private boolean btnHovered      = false;
    private boolean btnPressed      = false;

    // ─────────────────────────────────────────────────────────────────────────

    public StaffLoginPanel(ApplicationFrame frame) {
        super(ThemeService.getInstance().getPublicImage(),
              ImageBackgroundPanel.Treatment.FULL_OVERLAY, 0.72f, Color.BLACK);
        this.frame = frame;
        this.auth  = AuthService.getInstance();

        setLayout(null);
        initComponents();
        registerKeyBindings();
    }

    // ── Component init ────────────────────────────────────────────────────────

    private void initComponents() {
        usernameField = createStyledTextField("Username");
        add(usernameField);

        passwordField = createStyledPasswordField();
        add(passwordField);

        showHideLabel = new JLabel("\uD83D\uDC41");
        showHideLabel.setForeground(TEXT_MUTED);
        showHideLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        showHideLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        showHideLabel.setToolTipText("Show / hide password");
        showHideLabel.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { togglePasswordVisibility(); }
        });
        add(showHideLabel);

        errorLabel = new JLabel(" ");
        errorLabel.setForeground(ERR_COLOR);
        errorLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(errorLabel);

        loginButton = createLoginButton();
        add(loginButton);

        guestLink = new JLabel("Guest? Register or log in here");
        guestLink.setForeground(ACCENT);
        guestLink.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        guestLink.setHorizontalAlignment(SwingConstants.CENTER);
        guestLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        guestLink.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                frame.showPanel(ApplicationFrame.PANEL_GUEST_LOGIN);
            }
            @Override public void mouseEntered(MouseEvent e) { guestLink.setForeground(TEXT_PRIMARY); }
            @Override public void mouseExited(MouseEvent e)  { guestLink.setForeground(ACCENT); }
        });
        add(guestLink);
    }

    // ── Layout ────────────────────────────────────────────────────────────────

    @Override
    public void doLayout() {
        super.doLayout();
        int cx = (getWidth()  - CARD_W) / 2;
        int cy = (getHeight() - CARD_H) / 2;

        int fieldX    = cx + 40;
        int fieldW    = CARD_W - 80;
        int fieldH    = 40;
        int titleBase = cy + 90;

        usernameField.setBounds(fieldX, titleBase, fieldW, fieldH);

        int pwY = titleBase + fieldH + 16;
        passwordField.setBounds(fieldX, pwY, fieldW - 34, fieldH);
        showHideLabel.setBounds(fieldX + fieldW - 30, pwY + 12, 24, 18);

        int errY = pwY + fieldH + 8;
        errorLabel.setBounds(cx, errY, CARD_W, 18);

        int btnY = errY + 26;
        loginButton.setBounds(fieldX, btnY, fieldW, 44);

        guestLink.setBounds(cx, btnY + 56, CARD_W, 20);
    }

    // ── Paint ─────────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int cx = (getWidth()  - CARD_W) / 2;
        int cy = (getHeight() - CARD_H) / 2;

        RoundRectangle2D card = new RoundRectangle2D.Double(cx, cy, CARD_W, CARD_H, CARD_ARC, CARD_ARC);
        g2.setColor(CARD_BG);
        g2.fill(card);
        g2.setColor(CARD_BORDER);
        g2.draw(card);

        g2.setColor(ACCENT);
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        drawCenteredString(g2, "Deep Thought Entertainment Group", cx, cy + 32, CARD_W);

        g2.setColor(TEXT_PRIMARY);
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        drawCenteredString(g2, "Staff Portal", cx, cy + 60, CARD_W);

        g2.setColor(TEXT_MUTED);
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        drawCenteredString(g2, "Sign in with your network credentials", cx, cy + 82, CARD_W);

        g2.dispose();
    }

    // ── Login action ──────────────────────────────────────────────────────────

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        clearError();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password are required.");
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Authenticating…");

        // BCrypt is CPU-bound — run off the EDT
        new Thread(() -> {
            try {
                // ── TODO (Anan): replace stub with PersistenceService lookup ──
                // UserRecord user = PersistenceService.getInstance().findUserByUsername(username);
                // if (user == null || !auth.verifyPassword(password, user.getPasswordHash())) {
                //     throw new SecurityException("Invalid username or password.");
                // }
                // String token  = auth.issueJWT(user.getUserId(), user.getRole(),
                //                               user.getOrgId(), user.getEnterpriseId(),
                //                               user.getEmail());
                // Claims claims = auth.validateJWT(token);
                // SessionManager.login(token, claims);

                // ── Stub — remove once PersistenceService is live ─────────────
                if (!"admin".equals(username) || !"admin".equals(password)) {
                    throw new SecurityException("Invalid username or password.");
                }
                String token = auth.issueJWT(
                    "demo-001", Claims.roleNetworkAdmin,
                    "magratheaStudios", "magratheaStudios",
                    "admin@deepthought.com"
                );
                Claims claims = auth.validateJWT(token);
                SessionManager.login(token, claims);
                // ── End stub ──────────────────────────────────────────────────

                SwingUtilities.invokeLater(() -> {
                    loginButton.setEnabled(true);
                    loginButton.setText("Sign In");
                    usernameField.setText("");
                    passwordField.setText("");
                    frame.routeByRole();
                });

            } catch (SecurityException ex) {
                SwingUtilities.invokeLater(() -> {
                    loginButton.setEnabled(true);
                    loginButton.setText("Sign In");
                    showError(ex.getMessage());
                    passwordField.setText("");
                    passwordField.requestFocusInWindow();
                });
            }
        }, "auth-thread").start();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        passwordField.setEchoChar(passwordVisible ? (char) 0 : '\u2022');
        showHideLabel.setText(passwordVisible ? "\uD83D\uDC41\u200D\uD83D\uDDE8" : "\uD83D\uDC41");
    }

    private void showError(String msg) { errorLabel.setText(msg); }
    private void clearError()          { errorLabel.setText(" "); }

    private void registerKeyBindings() {
        KeyAdapter enterKey = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) attemptLogin();
            }
        };
        usernameField.addKeyListener(enterKey);
        passwordField.addKeyListener(enterKey);
    }

    private static void drawCenteredString(Graphics2D g2, String text, int x, int y, int width) {
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, x + (width - fm.stringWidth(text)) / 2, y);
    }

    /** Called by ApplicationFrame.showPanel() — resets state and focuses username. */
    public void onShow() {
        clearError();
        passwordField.setText("");
        SwingUtilities.invokeLater(() -> usernameField.requestFocusInWindow());
    }

    // ── Field / button factories ──────────────────────────────────────────────

    private JTextField createStyledTextField(String placeholder) {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FIELD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isFocusOwner() ? FIELD_FOCUS : FIELD_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        f.setOpaque(false);
        f.setForeground(TEXT_MUTED);
        f.setCaretColor(TEXT_PRIMARY);
        f.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        f.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        f.setText(placeholder);

        f.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                if (f.getText().equals(placeholder)) {
                    f.setText("");
                    f.setForeground(TEXT_PRIMARY);
                }
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (f.getText().isEmpty()) {
                    f.setText(placeholder);
                    f.setForeground(TEXT_MUTED);
                }
            }
        });
        return f;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField f = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FIELD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isFocusOwner() ? FIELD_FOCUS : FIELD_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        f.setOpaque(false);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(TEXT_PRIMARY);
        f.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        f.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        f.setEchoChar('\u2022');
        return f;
    }

    private JButton createLoginButton() {
        JButton btn = new JButton("Sign In") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = btnPressed ? BTN_PRESS : btnHovered ? BTN_HOVER : BTN_NORMAL;
                g2.setColor(isEnabled() ? bg : new Color(50, 50, 90));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener((ActionEvent e) -> attemptLogin());
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e)  { btnHovered = true;  btn.repaint(); }
            @Override public void mouseExited(MouseEvent e)   { btnHovered = false; btnPressed = false; btn.repaint(); }
            @Override public void mousePressed(MouseEvent e)  { btnPressed = true;  btn.repaint(); }
            @Override public void mouseReleased(MouseEvent e) { btnPressed = false; btn.repaint(); }
        });
        return btn;
    }
}