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
    private static final int   cardW       = 480;
    private static final int   cardH       = 560;
    private static final int   cardArc     = 20;
    private static final Color cardBg      = new Color(10, 10, 26, 210);
    private static final Color cardBorder  = new Color(80, 80, 140, 180);
    private static final Color accent      = ThemeService.colorAccentPurple;
    private static final Color textPrimary = ThemeService.colorTextPrimary;
    private static final Color textMuted   = ThemeService.colorTextMuted;
    private static final Color fieldBg     = new Color(20, 20, 46, 230);
    private static final Color fieldBorder = new Color(60, 60, 110, 200);
    private static final Color fieldFocus  = new Color(100, 120, 220, 180);
    private static final Color errColor    = new Color(220, 80, 80);
    private static final Color btnNormal   = new Color(70, 90, 200);
    private static final Color btnHover    = new Color(90, 110, 230);
    private static final Color btnPress    = new Color(50, 70, 170);

    // ── Services / frame ─────────────────────────────────────────────────────
    private final ApplicationFrame frame;
    private final AuthService       auth;

    // ── UI components ─────────────────────────────────────────────────────────
    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JLabel         passwordPlaceholder;
    private JLabel         errorLabel;
    private JLabel         showHideLabel;
    private JButton        loginButton;
    private JButton        backButton;
    private JLabel         guestLink;

    // ── State ─────────────────────────────────────────────────────────────────
    private boolean passwordVisible = false;
    private boolean isBtnHovered    = false;
    private boolean isBtnPressed    = false;

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

        passwordPlaceholder = new JLabel("Password");
        passwordPlaceholder.setForeground(ThemeService.colorTextSecondary);
        passwordPlaceholder.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        add(passwordPlaceholder);
        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                passwordPlaceholder.setVisible(false);
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                passwordPlaceholder.setVisible(passwordField.getPassword().length == 0);
            }
        });

        showHideLabel = new JLabel("\uD83D\uDC41");
        showHideLabel.setForeground(textPrimary);
        showHideLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        showHideLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 22));
        showHideLabel.setToolTipText("Show / hide password");
        showHideLabel.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { togglePasswordVisibility(); }
        });
        add(showHideLabel);

        errorLabel = new JLabel(" ");
        errorLabel.setForeground(errColor);
        errorLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(errorLabel);

        loginButton = createLoginButton();
        add(loginButton);

        backButton = new JButton("\u2190  Back");
        backButton.setForeground(textMuted);
        backButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        backButton.setOpaque(false);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { backButton.setForeground(textPrimary); }
            @Override public void mouseExited(MouseEvent e)  { backButton.setForeground(textMuted); }
        });
        backButton.addActionListener(e -> frame.showPanel(ApplicationFrame.panelSplash));
        add(backButton);

        guestLink = new JLabel("Guest? Register or log in here");
        guestLink.setForeground(accent);
        guestLink.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        guestLink.setHorizontalAlignment(SwingConstants.CENTER);
        guestLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        guestLink.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                frame.showPanel(ApplicationFrame.panelGuestLogin);
            }
            @Override public void mouseEntered(MouseEvent e) { guestLink.setForeground(textPrimary); }
            @Override public void mouseExited(MouseEvent e)  { guestLink.setForeground(accent); }
        });
        add(guestLink);
    }

    // ── Layout ────────────────────────────────────────────────────────────────

    @Override
    public void doLayout() {
        super.doLayout();
        int cx = (getWidth()  - cardW) / 2;
        int cy = (getHeight() - cardH) / 2;

        int fieldX    = cx + 40;
        int fieldW    = cardW - 80;
        int fieldH    = 46;

        // Stack from card top with fixed gaps — resolution independent
        int titleBase = cy + 110;
        int pwY       = titleBase + fieldH + 20;
        int errY      = pwY + fieldH + 10;
        int btnY      = errY + 28;
        int linkY     = btnY + 58;

        usernameField.setBounds(fieldX, titleBase, fieldW, fieldH);
        passwordField.setBounds(fieldX, pwY, fieldW - 40, fieldH);
        passwordPlaceholder.setBounds(fieldX + 12, pwY, fieldW - 60, fieldH);
        showHideLabel.setBounds(fieldX + fieldW - 34, pwY + 10, 28, 28);
        errorLabel.setBounds(cx, errY, cardW, 18);
        loginButton.setBounds(fieldX, btnY, fieldW, 48);
        backButton.setBounds(cx, linkY, cardW / 2, 24);
        guestLink.setBounds(cx + cardW / 2, linkY, cardW / 2, 24);
    }

    // ── Paint ─────────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int cx = (getWidth()  - cardW) / 2;
        int cy = (getHeight() - cardH) / 2;

        RoundRectangle2D card = new RoundRectangle2D.Double(cx, cy, cardW, cardH, cardArc, cardArc);
        g2.setColor(cardBg);
        g2.fill(card);
        g2.setColor(cardBorder);
        g2.draw(card);

        g2.setColor(accent);
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        drawCenteredString(g2, "Deep Thought Entertainment Group", cx, cy + 40, cardW);

        g2.setColor(textPrimary);
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));
        drawCenteredString(g2, "Staff Portal", cx, cy + 78, cardW);

        g2.setColor(textMuted);
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        drawCenteredString(g2, "Sign in with your network credentials", cx, cy + 104, cardW);

        g2.dispose();
    }

    // ── Login action ──────────────────────────────────────────────────────────

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        clearError();

        if (username.isEmpty() || password.isEmpty()
                || username.equals("Username")) {
            showError("Username and password are required.");
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Authenticating…");

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
        passwordPlaceholder.setVisible(true);
        SwingUtilities.invokeLater(() -> usernameField.requestFocusInWindow());
    }

    // ── Field / button factories ──────────────────────────────────────────────

    private JTextField createStyledTextField(String placeholder) {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(fieldBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isFocusOwner() ? fieldFocus : fieldBorder);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        f.setOpaque(false);
        f.setCaretColor(textPrimary);
        f.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        f.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        f.setForeground(ThemeService.colorTextSecondary);
        f.setText(placeholder);

        f.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                if (f.getText().equals(placeholder)) {
                    f.setText("");
                    f.setForeground(textPrimary);
                }
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (f.getText().isEmpty()) {
                    f.setText(placeholder);
                    f.setForeground(ThemeService.colorTextSecondary);
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
                g2.setColor(fieldBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isFocusOwner() ? fieldFocus : fieldBorder);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        f.setOpaque(false);
        f.setForeground(textPrimary);
        f.setCaretColor(textPrimary);
        f.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        f.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        f.setEchoChar('\u2022');
        return f;
    }

    private JButton createLoginButton() {
        JButton btn = new JButton("Sign In") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = isBtnPressed ? btnPress : isBtnHovered ? btnHover : btnNormal;
                g2.setColor(isEnabled() ? bg : new Color(50, 50, 90));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener((ActionEvent e) -> attemptLogin());
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e)  { isBtnHovered = true;  btn.repaint(); }
            @Override public void mouseExited(MouseEvent e)   { isBtnHovered = false; isBtnPressed = false; btn.repaint(); }
            @Override public void mousePressed(MouseEvent e)  { isBtnPressed = true;  btn.repaint(); }
            @Override public void mouseReleased(MouseEvent e) { isBtnPressed = false; btn.repaint(); }
        });
        return btn;
    }
}