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
 * Work area panel for the Marketing Lead role.
 * Displays active campaigns, pending approvals, and work requests
 * scoped to the current user's organization.
 */
public class MarketingLeadPanel extends JPanel {

    // ── Configuration ─────────────────────────────────────────────────────────
    private static final String requiredRole = Claims.roleMarketingLead;
    private static final String pageTitle    = "Marketing Lead Dashboard";
    private static final String pageSubtitle = "Marketing Operations";

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
    private JLabel      titleLabel;
    private JLabel      subtitleLabel;
    private JPanel      statsRow;
    private JPanel      toolbar;
    private JTable      dataTable;
    private JScrollPane tableScroll;
    private JPanel      mainContent;

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Constructs the Marketing Lead work area panel.
     *
     * @param frame the parent ApplicationFrame used for panel navigation
     */
    public MarketingLeadPanel(ApplicationFrame frame) {
        this.frame = frame;
        setBackground(bgPrimary);
        setLayout(new BorderLayout(0, 0));
        buildComponents();
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
        statsRow.add(buildStatCard("Active Campaigns",  "—"));
        statsRow.add(buildStatCard("Pending Approvals", "—"));
        statsRow.add(buildStatCard("Work Requests",     "—"));

        toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(bgPrimary);
        toolbar.setBorder(new EmptyBorder(0, 0, 12, 0));
        toolbar.add(buildToolbarButton("+ New"));
        toolbar.add(buildToolbarButton("Export"));

        String[] columns = { "WR ID", "Title", "Department", "Type", "Status" };
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        dataTable = new JTable(tableModel);
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

        mainContent = new JPanel(new BorderLayout(0, 0));
        mainContent.setBackground(bgPrimary);
        mainContent.add(toolbar,     BorderLayout.NORTH);
        mainContent.add(tableScroll, BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout(0, 0));
        top.setBackground(bgPrimary);
        top.add(header,   BorderLayout.NORTH);
        top.add(statsRow, BorderLayout.SOUTH);

        wrapper.add(top,         BorderLayout.NORTH);
        wrapper.add(mainContent, BorderLayout.CENTER);

        add(wrapper, BorderLayout.CENTER);
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    /**
     * Called by ApplicationFrame.showPanel() when this panel becomes visible.
     * Guards the session then loads data.
     */
    public void onShow() {
        if (!SessionManager.guard(requiredRole)) {
            frame.showPanel(ApplicationFrame.panelStaffLogin);
            return;
        }
        updateSubtitle();
        loadData();
    }

    /**
     * Loads mock campaign and work request data into the table and updates stat cards.
     * TODO: replace with real PersistenceService calls.
     */
    protected void loadData() {
        setStatValue(0, "4");
        setStatValue(1, "1");
        setStatValue(2, "5");

        DefaultTableModel m = (DefaultTableModel) dataTable.getModel();
        m.setRowCount(0);
        m.addRow(new Object[]{ "WR-501", "Q3 Retail Campaign",   "Retail",    "Marketing", "Active" });
        m.addRow(new Object[]{ "WR-502", "Broadcast Promo",      "Broadcast", "Marketing", "Pending Approval" });
        m.addRow(new Object[]{ "WR-503", "Digital Ads Refresh",  "Digital",   "Marketing", "Open" });
    }

    // ── Subtitle ──────────────────────────────────────────────────────────────

    /**
     * Updates the subtitle to reflect the current user's org from the JWT session.
     */
    private void updateSubtitle() {
        String org = SessionManager.getOrgId();
        subtitleLabel.setText(org != null && !org.isBlank()
            ? formatCamelCase(org) : pageSubtitle);
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

    // ── Utility ───────────────────────────────────────────────────────────────

    /**
     * Splits a camelCase string into readable display text.
     *
     * @param s camelCase input string
     * @return space-separated display string
     */
    private static String formatCamelCase(String s) {
        if (s == null || s.isBlank()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isUpperCase(c) && i > 0) sb.append(' ');
            sb.append(i == 0 ? Character.toUpperCase(c) : c);
        }
        return sb.toString();
    }
}