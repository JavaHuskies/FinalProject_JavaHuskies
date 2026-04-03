package ui.panels;

import model.Casino.CasinoSession;
import model.Casino.GameRound;
import model.Casino.RouletteRules.Bet;
import model.Casino.RouletteRules.BetType;
import service.SessionManager;
import service.ThemeService;
import ui.ApplicationFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GuestCasinoPanel extends JPanel {

    private static final Color bgPrimary     = ThemeService.colorBgPrimary;
    private static final Color bgSecondary   = ThemeService.colorBgSecondary;
    private static final Color bgTertiary    = ThemeService.colorBgTertiary;
    private static final Color textPrimary   = ThemeService.colorTextPrimary;
    private static final Color textSecondary = ThemeService.colorTextSecondary;
    private static final Color textMuted     = ThemeService.colorTextMuted;
    private static final Color borderColor   = ThemeService.colorBorder;

    private final ApplicationFrame frame;

    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JTable table;
    private DefaultTableModel roundsModel;
    private JLabel resultLabel;

    private JComboBox<BetType> betTypeCombo;
    private JSpinner betAmountSpinner;

    private CasinoSession session;

    public GuestCasinoPanel(ApplicationFrame frame) {
        this.frame = frame;
        setBackground(bgPrimary);
        setLayout(new BorderLayout());
        buildComponents();
        startNewSession();
    }

    public void onShow() {
        if (!SessionManager.isGuest()) {
            frame.showPanel(ApplicationFrame.panelGuestLogin);
            return;
        }
        updateHeader();
        updateStats();
    }

    private void startNewSession() {
        String userId = SessionManager.getUserId();
        this.session = new CasinoSession(
                java.util.UUID.randomUUID().toString(),
                userId != null ? userId : "guest",
                1000
        );
    }

    private void buildComponents() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        wrapper.setBackground(bgPrimary);
        wrapper.setBorder(new EmptyBorder(32, 80, 24, 80));

        JPanel header = new JPanel(new BorderLayout(0, 4));
        header.setBackground(bgPrimary);

        titleLabel = new JLabel("Guest Casino");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        titleLabel.setForeground(textPrimary);

        subtitleLabel = new JLabel("—");
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        subtitleLabel.setForeground(textMuted);

        header.add(titleLabel, BorderLayout.NORTH);
        header.add(subtitleLabel, BorderLayout.SOUTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(bgPrimary);

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
        toolbar.add(resetButton);

        String[] cols = { "Round", "Bet Type", "Amount", "Result", "Payout", "Balance After" };
        roundsModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(roundsModel);
        table.setBackground(bgSecondary);
        table.setForeground(textSecondary);
        table.setSelectionBackground(bgTertiary);
        table.setSelectionForeground(textPrimary);
        table.setGridColor(borderColor);
        table.setRowHeight(36);
        table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        table.setShowVerticalLines(false);
        table.setFocusable(false);

        table.getTableHeader().setBackground(bgTertiary);
        table.getTableHeader().setForeground(textMuted);
        table.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        table.getTableHeader().setBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(bgSecondary);
        scrollPane.getViewport().setBackground(bgSecondary);
        scrollPane.setBorder(BorderFactory.createLineBorder(borderColor, 1));

        resultLabel = new JLabel("Place your bet and spin.");
        resultLabel.setForeground(textPrimary);
        resultLabel.setBorder(new EmptyBorder(12, 0, 0, 0));

        JPanel mainContent = new JPanel(new BorderLayout(0, 0));
        mainContent.setBackground(bgPrimary);
        mainContent.add(toolbar, BorderLayout.NORTH);
        mainContent.add(scrollPane, BorderLayout.CENTER);
        mainContent.add(resultLabel, BorderLayout.SOUTH);

        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(mainContent, BorderLayout.CENTER);

        add(wrapper, BorderLayout.CENTER);
    }

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
            } catch (Exception ex) {
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

    private void resetGame() {
        startNewSession();
        roundsModel.setRowCount(0);
        resultLabel.setText("New session started.");
        updateStats();
    }

    private void updateStats() {
        // No stat cards in this panel — placeholder for future expansion
    }

    private void updateHeader() {
        subtitleLabel.setText("Guest: " + SessionManager.getUserId());
    }

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
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(bgSecondary); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(bgTertiary); }
        });
        return btn;
    }
}
