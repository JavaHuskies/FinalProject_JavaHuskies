package ui.panels;

import model.Casino.CasinoSession;
import model.Casino.GameRound;
import model.Casino.RouletteRules;
import model.Casino.RouletteRules.Bet;
import model.Casino.RouletteRules.BetType;
import service.SessionManager;
import ui.ApplicationFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CasinoPanel extends WorkAreaTemplate {

    private CasinoSession session;

    private JComboBox<BetType> betTypeCombo;
    private JSpinner betAmountSpinner;
    private JLabel resultLabel;
    private DefaultTableModel roundsModel;

    public CasinoPanel(ApplicationFrame frame) {
        super(frame);

        setPageTitle("Casino");
        setPageSubtitle("Roulette Table");

        startNewSession();
        initControls();
        configureTable();
        updateStats();
    }

    private void startNewSession() {
        String userId = SessionManager.getUserId();
        this.session = new CasinoSession(
            java.util.UUID.randomUUID().toString(),
            userId != null ? userId : "anonymous",
            1000
        );
    }

    private void initControls() {
        betTypeCombo = new JComboBox<>(BetType.values());

        betAmountSpinner = new JSpinner(
            new SpinnerNumberModel(10, 1, 1000, 1)
        );

        JButton spinButton = buildToolbarButton("Spin");
        spinButton.addActionListener(e -> handleSpin());

        JButton resetButton = buildToolbarButton("Reset");
        resetButton.addActionListener(e -> resetGame());

        JPanel toolbar = getToolbar();
        toolbar.removeAll();
        toolbar.add(new JLabel("Bet Type:"));
        toolbar.add(betTypeCombo);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(new JLabel("Amount:"));
        toolbar.add(betAmountSpinner);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(spinButton);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(resetButton);

        resultLabel = new JLabel("Place your bet and spin.");
        resultLabel.setForeground(Color.WHITE);

        JPanel mainContent = getMainContent();
        mainContent.add(resultLabel, BorderLayout.SOUTH);
    }

    private void configureTable() {
        String[] cols = { "Round", "Bet Type", "Amount", "Result", "Payout", "Balance After" };
        roundsModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        getDataTable().setModel(roundsModel);
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

    private void resetGame() {
        startNewSession();
        roundsModel.setRowCount(0);
        resultLabel.setText("New session started.");
        updateStats();
    }

    private void updateStats() {
        int totalSpins = session.getRounds().size();
        int totalWins = (int) session.getRounds().stream()
            .filter(r -> r.getPayout() > 0)
            .count();
        int netProfit = session.getBalance() - 1000;

        setStatValue(0, String.valueOf(totalSpins));
        setStatValue(1, String.valueOf(totalWins));
        setStatValue(2, "$" + netProfit);
    }

    @Override
    public void onShow() {
        updateStats();
    }
}
