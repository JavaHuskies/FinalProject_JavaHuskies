package ui.panels;

import model.Claims;
import service.SessionManager;
import service.ThemeService;
import ui.ApplicationFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Guest Casino Panel — accessible only by guest users.
 *
 * Layout: stats row + toolbar + tabbed pane (Games | Bookings | Rewards)
 */
public class GuestCasinoPanel extends JPanel {

    // Colors
    private static final Color bgPrimary     = ThemeService.colorBgPrimary;
    private static final Color bgSecondary   = ThemeService.colorBgSecondary;
    private static final Color bgTertiary    = ThemeService.colorBgTertiary;
    private static final Color textPrimary   = ThemeService.colorTextPrimary;
    private static final Color textSecondary = ThemeService.colorTextSecondary;
    private static final Color textMuted     = ThemeService.colorTextMuted;
    private static final Color borderColor   = ThemeService.colorBorder;

    private static final String mockTip = "Mock data — replace with PersistenceService query";

    // Frame
    private final ApplicationFrame frame;

    // Components
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JPanel statsRow;
    private JTabbedPane tabs;

    private JTable gamesTable;
    private JTable bookingsTable;
    private JTable rewardsTable;

    public GuestCasinoPanel(ApplicationFrame frame) {
        this.frame = frame;
        setBackground(bgPrimary);
        setLayout(new BorderLayout(0, 0));
        buildComponents();
    }

    // Lifecycle
    public void onShow() {
        if (!SessionManager.isGuest()) {
            frame.showPanel(ApplicationFrame.panelGuestLogin);
            return;
        }
        updateHeader();
        loadData();
    }

    // Build UI
    private void buildComponents() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        wrapper.setBackground(bgPrimary);
        wrapper.setBorder(new EmptyBorder(32, 80, 24, 80));

        // Header
        JPanel header = new JPanel(new BorderLayout(0, 4));
        header.setBackground(bgPrimary);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        titleLabel = new JLabel("Guest Casino");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        titleLabel.setForeground(textPrimary);

        subtitleLabel = new JLabel("Welcome, Guest");
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        subtitleLabel.setForeground(textMuted);

        header.add(titleLabel, BorderLayout.NORTH);
        header.add(subtitleLabel, BorderLayout.SOUTH);

        // Stats row
        statsRow = new JPanel(new GridLayout(1, 3, 12, 0));
        statsRow.setBackground(bgPrimary);
        statsRow.setBorder(new EmptyBorder(0, 0, 20, 0));
        statsRow.add(buildStatCard("Available Games", "—"));
        statsRow.add(buildStatCard("Upcoming Bookings", "—"));
        statsRow.add(buildStatCard("Reward Points", "—"));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(bgPrimary);
        toolbar.setBorder(new EmptyBorder(0, 0, 12, 0));
        toolbar.add(buildToolbarButton("Book Experience"));
        toolbar.add(buildToolbarButton("Redeem Rewards"));
        toolbar.add(buildToolbarButton("Refresh"));

        // Tabs
        tabs = new JTabbedPane();
        tabs.setBackground(bgSecondary);
        tabs.setForeground(textMuted);
        tabs.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        tabs.setBorder(BorderFactory.createLineBorder(borderColor, 1));

        tabs.addTab("Games", buildGamesTab());
        tabs.addTab("Bookings", buildBookingsTab());
        tabs.addTab("Rewards", buildRewardsTab());

        // Main content
        JPanel mainContent = new JPanel(new BorderLayout(0, 0));
        mainContent.setBackground(bgPrimary);
        mainContent.add(toolbar, BorderLayout.NORTH);
        mainContent.add(tabs, BorderLayout.CENTER);

        // Assemble
        JPanel top = new JPanel(new BorderLayout(0, 0));
        top.setBackground(bgPrimary);
        top.add(header, BorderLayout.NORTH);
        top.add(statsRow, BorderLayout.SOUTH);

        wrapper.add(top, BorderLayout.NORTH);
        wrapper.add(mainContent, BorderLayout.CENTER);

        add(wrapper, BorderLayout.CENTER);
    }

    // Tab builders
    private JScrollPane buildGamesTab() {
        String[] cols = { "Game", "Type", "Min Bet", "Status" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        gamesTable = styledTable(model);
        JScrollPane sp = styledScroll(gamesTable);
        sp.setToolTipText(mockTip);
        return sp;
    }

    private JScrollPane buildBookingsTab() {
        String[] cols = { "Booking ID", "Experience", "Date", "Status" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        bookingsTable = styledTable(model);
        JScrollPane sp = styledScroll(bookingsTable);
        sp.setToolTipText(mockTip);
        return sp;
    }

    private JScrollPane buildRewardsTab() {
        String[] cols = { "Reward", "Points Required", "Status" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        rewardsTable = styledTable(model);
        JScrollPane sp = styledScroll(rewardsTable);
        sp.setToolTipText(mockTip);
        return sp;
    }

    // Data loading
    private void loadData() {
        loadGames();
        loadBookings();
        loadRewards();
        updateStats();
    }

    private void loadGames() {
        DefaultTableModel m = (DefaultTableModel) gamesTable.getModel();
        m.setRowCount(0);
        m.addRow(new Object[]{ "Galactic Slots", "Slots", "$1", "Open" });
        m.addRow(new Object[]{ "Quantum Roulette", "Table", "$5", "Open" });
        m.addRow(new Object[]{ "Nebula Poker", "Cards", "$10", "Closed" });
    }

    private void loadBookings() {
        DefaultTableModel m = (DefaultTableModel) bookingsTable.getModel();
        m.setRowCount(0);
        m.addRow(new Object[]{ "B-101", "Vogon Poetry Escape Room", "2026-04-01", "Confirmed" });
        m.addRow(new Object[]{ "B-102", "Pan-Galactic Gargle Blaster Tasting", "2026-04-03", "Pending" });
    }

    private void loadRewards() {
        DefaultTableModel m = (DefaultTableModel) rewardsTable.getModel();
        m.setRowCount(0);
        m.addRow(new Object[]{ "Free Drink", "100", "Available" });
        m.addRow(new Object[]{ "VIP Lounge Access", "500", "Locked" });
    }

    private void updateStats() {
        setStatValue(0, "3");
        setStatValue(1, "2");
        setStatValue(2, "120");
    }

    private void updateHeader() {
        subtitleLabel.setText("Guest ID: " + SessionManager.getUserId());
    }

    // Helpers
    private void setStatValue(int index, String value) {
        JPanel card = (JPanel) statsRow.getComponent(index);
        for (Component c : card.getComponents()) {
            if (c instanceof JLabel lbl && lbl.getFont().getSize() >= 22) {
                lbl.setText(value);
                break;
            }
        }
    }

    private JPanel buildStatCard(String label, String value) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(bgSecondary);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                new EmptyBorder(14, 16, 14, 16)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.anchor = GridBagConstraints.CENTER;

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

    private JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
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

        return table;
    }

    private JScrollPane styledScroll(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBackground(bgSecondary);
        sp.getViewport().setBackground(bgSecondary);
        sp.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        return sp;
    }
}
