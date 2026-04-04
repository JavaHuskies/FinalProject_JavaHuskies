package ui.panels;

import model.Claims;
import service.SessionManager;
import service.ThemeService;
import ui.ApplicationFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;

/**
 * Work area panel for the Group CFO role.
 * Displays network-level financial data — revenue by organization,
 * work request cost exposure, and guest revenue. Read-only.
 */
public class CfoPanel extends JPanel {

    // ── Configuration ─────────────────────────────────────────────────────────
    private static final String requiredRole = Claims.roleGroupCfo;
    private static final String pageTitle    = "CFO Dashboard";
    private static final String pageSubtitle = "Enterprise Financial Overview";

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
     * Constructs the CFO work area panel.
     *
     * @param frame the parent ApplicationFrame used for panel navigation
     */
    public CfoPanel(ApplicationFrame frame) {
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
        statsRow.add(buildStatCard("Total Network Revenue",  "—"));
        statsRow.add(buildStatCard("Open WR Cost Exposure",  "—"));
        statsRow.add(buildStatCard("Guest Revenue MTD",      "—"));

        toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(bgPrimary);
        toolbar.setBorder(new EmptyBorder(0, 0, 12, 0));

        JButton refresh = buildToolbarButton("Refresh");
        refresh.addActionListener(e -> loadData());
        JButton export = buildToolbarButton("Export");
        export.addActionListener(e -> exportCsv());

        toolbar.add(refresh);
        toolbar.add(export);

        String[] cols = { "Organization", "Budget", "Spend", "Variance", "Status" };
        DefaultTableModel tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
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
        loadData();
    }

    /**
     * Loads mock financial data into the table and updates stat cards.
     * TODO: replace with real PersistenceService calls.
     */
    protected void loadData() {
        DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
        model.setRowCount(0);
        model.addRow(new Object[]{ "Slartibartfast Pictures", "$1.2M", "$980k",  "$220k",  "On Track" });
        model.addRow(new Object[]{ "Magrathea Studios",       "$3.4M", "$3.8M",  "-$400k", "Over Budget" });
        model.addRow(new Object[]{ "Pan Galactic Broadcast",  "$2.1M", "$1.9M",  "$200k",  "On Track" });

        setStatValue(0, "$6.7M");
        setStatValue(1, "$6.68M");
        setStatValue(2, "$20k");
    }

    // ── Export ────────────────────────────────────────────────────────────────

    /**
     * Exports the current table contents to a user-selected CSV file.
     */
    private void exportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export CFO Report");
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (PrintWriter out = new PrintWriter(file)) {
                DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
                for (int c = 0; c < model.getColumnCount(); c++) {
                    out.print(model.getColumnName(c));
                    if (c < model.getColumnCount() - 1) out.print(",");
                }
                out.println();
                for (int r = 0; r < model.getRowCount(); r++) {
                    for (int c = 0; c < model.getColumnCount(); c++) {
                        out.print(model.getValueAt(r, c));
                        if (c < model.getColumnCount() - 1) out.print(",");
                    }
                    out.println();
                }
                JOptionPane.showMessageDialog(
                        this,
                        "Export complete:\n" + file.getAbsolutePath(),
                        "Export Successful",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Export failed:\n" + ex.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
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