package ui.panels;

import model.Casino.CasinoSession;
import model.Casino.GameRound;
import model.Casino.RouletteRules.Bet;
import model.Casino.RouletteRules.BetType;
import model.Claims;
import service.SessionManager;
import service.ThemeService;
import ui.ApplicationFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Work area panel for the Milliways Casino guest experience.
 * Restricted to authenticated guests with a confirmed Milliways booking.
 * Supports roulette betting with full session and round history tracking.
 */
public class CasinoOpsPanel extends JPanel {

    // ── Configuration ─────────────────────────────────────────────────────────
    private static final String requiredRole = Claims.roleGuest;
    private static final String pageTitle    = "Casino";
    private static final String pageSubtitle = "Roulette Table";

    // ── Colors ────────────────────────────────────────────────────────────────
    private static final Color bgPrimary     = ThemeService.colorBgPrimary;
    private static final Color bgSecondary   = ThemeService.colorBgSecondary;
    private static final Color bgTertiary    = ThemeService.colorBgTertiary;
    private static final Color textPrimary   = ThemeService.colorTextPrimary;
    private static final Color textSecondary = ThemeService.colorTextSecondary;
    private static final Color textMuted     = ThemeService.colorTextMuted;
    private static final Color accentPurple  = ThemeService.colorAccentPurple;
    private static final Color borderColor   = ThemeService.colorBorder;

    // ── Frame reference ───────────────────────────────────────────────────────
    private final ApplicationFrame frame;

    // ── UI components ─────────────────────────────────────────────────────────
    private JLabel           titleLabel;
    private JLabel           subtitleLabel;
    private JPanel           statsRow;
    private JPanel           toolbar;
    private JTable           dataTable;
    private JScrollPane      tableScroll;
    private JPanel           mainContent;
    private JLabel           resultLabel;
    private JComboBox<BetType> betTypeCombo;
    private JSpinner         betAmountSpinner;
    private DefaultTableModel roundsModel;

    // ── Session ───────────────────────────────────────────────────────────────
    private CasinoSession session;

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Constructs the Casino work area panel.
     *
     * @param frame the parent ApplicationFrame used for panel navigation
     */
    public CasinoOpsPanel(ApplicationFrame frame) {
        this.frame = frame;
        setBackground(bgPrimary);
        setLayout(new BorderLayout(0, 0));
        buildComponents();
        startNewSession();
        configureTable();
        initControls();
        updateStats();
    }

    // ── Build ─────────────────────────────────────────────────────────────────

    /**
     * Builds all UI components.
     */
    private void buildComponents() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        wrapper.setBackground(bgPrimary);
        wrapper.setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel header = new JPanel(new BorderLayout(0, 4));
        header.setBackground(bgPrimary);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        titleLabel = new JLabel(pageTitle);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        titleLabel.setForeground(textPrimary);

        subtitleLabel = new JLabel(pageSubtitle);
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        subtitleLabel.setForeground(textMuted);

        header.add(titleLabel,    BorderLayout.NORTH);
        header.add(subtitleLabel, BorderLayout.SOUTH);

        statsRow = new JPanel(new GridLayout(1, 3, 12, 0));
        statsRow.setBackground(bgPrimary);
        statsRow.setBorder(new EmptyBorder(0, 0, 16, 0));
        statsRow.add(buildStatCard("Total Spins", "—"));
        statsRow.add(buildStatCard("Wins",        "—"));
        statsRow.add(buildStatCard("Net Profit",  "—"));

        toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(bgPrimary);
        toolbar.setBorder(new EmptyBorder(0, 0, 12, 0));

        String[] cols = { "Round", "Bet Type", "Amount", "Result", "Payout", "Balance After" };
        roundsModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        dataTable = new JTable(roundsModel);
        dataTable.setBackground(bgSecondary);
        dataTable.setForeground(textSecondary);
        dataTable.setSelectionBackground(bgTertiary);
        dataTable.setSelectionForeground(textPrimary);
        dataTable.setGridColor(borderColor);
        dataTable.setRowHeight(36);
        dataTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        dataTable.setShowVerticalLines(false);
        dataTable.setFocusable(false);

        dataTable.getTableHeader().setBackground(bgTertiary);
        dataTable.getTableHeader().setForeground(textMuted);
        dataTable.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        dataTable.getTableHeader().setBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor));

        tableScroll = new JScrollPane(dataTable);
        tableScroll.setBackground(bgSecondary);
        tableScroll.getViewport().setBackground(bgSecondary);
        tableScroll.setBorder(BorderFactory.createLineBorder(borderColor, 1));

        resultLabel = new JLabel("Place your bet and spin.");
        resultLabel.setForeground(textPrimary);
        resultLabel.setBorder(new EmptyBorder(8, 0, 0, 0));

        mainContent = new JPanel(new BorderLayout(0, 0));
        mainContent.setBackground(bgPrimary);
        mainContent.add(toolbar,      BorderLayout.NORTH);
        mainContent.add(tableScroll,  BorderLayout.CENTER);
        mainContent.add(resultLabel,  BorderLayout.SOUTH);

        JPanel top = new JPanel(new BorderLayout(0, 0));
        top.setBackground(bgPrimary);
        top.add(header,   BorderLayout.NORTH);
        top.add(statsRow, BorderLayout.SOUTH);

        wrapper.add(top,         BorderLayout.NORTH);
        wrapper.add(mainContent, BorderLayout.CENTER);

        add(wrapper, BorderLayout.CENTER);
    }

    // ── Controls ──────────────────────────────────────────────────────────────

    /**
     * Wires betting controls into the toolbar after buildComponents() runs.
     */
    private void initControls() {
        betTypeCombo = new JComboBox<>(BetType.values());
        betAmountSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));

        JButton spinButton = buildToolbarButton("Spin");
        spinButton.addActionListener(e -> handleSpin());

        JButton resetButton = buildToolbarButton("Reset");
        resetButton.addActionListener(e -> resetGame());

        toolbar.add(new JLabel("Bet Type:"));
        toolbar.add(betTypeCombo);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(new JLabel("Amount:"));
        toolbar.add(betAmountSpinner);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(spinButton);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(resetButton);
    }

    /**
     * Sets the table model columns on the data table.
     */
    private void configureTable() {
        dataTable.setModel(roundsModel);
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    /**
     * Called by ApplicationFrame.showPanel() when this panel becomes visible.
     * Guards the guest session then refreshes stats.
     */
    public void onShow() {
        if (!SessionManager.isGuest()) {
            frame.showPanel(ApplicationFrame.panelGuestLogin);
            return;
        }
        updateStats();
    }

    // ── Game logic ────────────────────────────────────────────────────────────

    /**
     * Starts a new casino session for the current guest user.
     */
    private void startNewSession() {
        String userId = SessionManager.getUserId();
        this.session = new CasinoSession(
            java.util.UUID.randomUUID().toString(),
            userId != null ? userId : "anonymous",
            1000
        );
    }

    /**
     * Handles a spin action — resolves the bet, updates the table and result label.
     */
    private void handleSpin() {
        BetType type = (BetType) betTypeCombo.getSelectedItem();
        int amount = (int) betAmountSpinner.getValue();

        int targetNumber = -1;
        if (type == BetType.SINGLE_NUMBER) {
            String input = JOptionPane.showInputDialog(
                this,
                "Enter number (0–36):",
                "Single Number Bet",
                JOptionPane.PLAIN_MESSAGE
            );
            if (input == null) return;
            try {
                targetNumber = Integer.parseInt(input);
                if (targetNumber < 0 || targetNumber > 36) {
                    JOptionPane.showMessageDialog(this, "Invalid number.");
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number.");
                return;
            }
        }

        Bet bet = new Bet(type, amount, targetNumber);
        GameRound round = session.playRoulette(bet);

        resultLabel.setText(
            "Result: " + round.getSpinResult() +
            "   |   Payout: " + round.getPayout() +
            "   |   Balance: $" + session.getBalance()
        );

        roundsModel.addRow(new Object[]{
            round.getRoundNumber(),
            type,
            amount,
            round.getSpinResult(),
            round.getPayout(),
            session.getBalance()
        });

        updateStats();
    }

    /**
     * Resets the game by starting a new session and clearing the round history.
     */
    private void resetGame() {
        startNewSession();
        roundsModel.setRowCount(0);
        resultLabel.setText("New session started.");
        updateStats();
    }

    /**
     * Updates stat cards from the current session state.
     */
    private void updateStats() {
        int totalSpins = session.getRounds().size();
        int totalWins  = (int) session.getRounds().stream()
            .filter(r -> r.getPayout() > 0).count();
        int netProfit  = session.getBalance() - 1000;

        setStatValue(0, String.valueOf(totalSpins));
        setStatValue(1, String.valueOf(totalWins));
        setStatValue(2, "$" + netProfit);
    }

    // ── Stat card helpers ─────────────────────────────────────────────────────

    /**
     * Updates a stat card value by index (0, 1, 2).
     *
     * @param index stat card position (0-based)
     * @param value display value to set
     */
    public void setStatValue(int index, String value) {
        if (index < 0 || index >= statsRow.getComponentCount()) return;
        JPanel card = (JPanel) statsRow.getComponent(index);
        for (Component c : card.getComponents()) {
            if (c instanceof JLabel lbl && lbl.getFont().getSize() >= 22) {
                lbl.setText(value);
                break;
            }
        }
    }

    // ── Component builders ────────────────────────────────────────────────────

    /**
     * Builds a single stat card displaying a metric label and value.
     *
     * @param label display label
     * @param value initial display value — use "—" for placeholder
     * @return styled JPanel stat card
     */
    private JPanel buildStatCard(String label, String value) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(bgSecondary);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            new EmptyBorder(14, 16, 14, 16)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.anchor = GridBagConstraints.WEST;

        JLabel labelLbl = new JLabel(label);
        labelLbl.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        labelLbl.setForeground(textMuted);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
        valueLbl.setForeground(textPrimary);

        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 4, 0);
        card.add(labelLbl, gbc);
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 0, 0);
        card.add(valueLbl, gbc);

        return card;
    }

    /**
     * Builds a styled toolbar button with hover effect.
     *
     * @param label button display text
     * @return configured JButton
     */
    private JButton buildToolbarButton(String label) {
        JButton btn = new JButton(label);
        btn.setBackground(bgTertiary);
        btn.setForeground(textPrimary);
        btn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            new EmptyBorder(6, 14, 6, 14)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(bgSecondary);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(bgTertiary);
            }
        });
        return btn;
    }
}