package ui.panels;

import model.Claims;
import service.SessionManager;
import service.ThemeService;
import ui.ApplicationFrame;
import ui.util.TableExportUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Work area panel for the Data Analyst role.
 * Displays record counts, processing time, and flagged items
 * scoped to the current user's enterprise.
 */
public class DataAnalystPanel extends JPanel {

    // ── Configuration ─────────────────────────────────────────────────────────
    private static final String requiredRole = Claims.roleDataAnalyst;
    private static final String pageTitle    = "Data Analyst Dashboard";
    private static final String pageSubtitle = "—";

    // ── Colors ────────────────────────────────────────────────────────────────
    private static final Color bgPrimary     = ThemeService.colorBgPrimary;
    private static final Color bgSecondary   = ThemeService.colorBgSecondary;
    private static final Color bgTertiary    = ThemeService.colorBgTertiary;
    private static final Color textPrimary   = ThemeService.colorTextPrimary;
    private static final Color textSecondary = ThemeService.colorTextSecondary;
    private static final Color textMuted     = ThemeService.colorTextMuted;
    private static final Color borderColor   = ThemeService.colorBorder;

    // ── Frame reference ───────────────────────────────────────────────────────
    private final ApplicationFrame frame;

    // ── UI components ─────────────────────────────────────────────────────────
    private JLabel      titleLabel;
    private JLabel      subtitleLabel;
    private JPanel      statsRow;
    private JTable      table;
    private JScrollPane scrollPane;

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Constructs the Data Analyst work area panel.
     *
     * @param frame the parent ApplicationFrame used for panel navigation
     */
    public DataAnalystPanel(ApplicationFrame frame) {
        this.frame = frame;
        setBackground(bgPrimary);
        setLayout(new BorderLayout());
        buildComponents();
    }

    // ── Build ─────────────────────────────────────────────────────────────────

    /**
     * Builds all UI components.
     */
    private void buildComponents() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        wrapper.setBackground(bgPrimary);
        wrapper.setBorder(new EmptyBorder(32, 80, 24, 80));

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
        statsRow.setBorder(new EmptyBorder(0, 0, 20, 0));
        statsRow.add(buildStatCard("Total Records",       "—"));
        statsRow.add(buildStatCard("Avg Processing Time", "—"));
        statsRow.add(buildStatCard("Flagged Items",       "—"));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(bgPrimary);
        toolbar.setBorder(new EmptyBorder(0, 0, 12, 0));

        JButton btnRefresh = buildToolbarButton("Refresh");
        JButton btnExport  = buildToolbarButton("Export");

        toolbar.add(btnRefresh);
        toolbar.add(btnExport);

        btnRefresh.addActionListener(e -> loadData());

        btnExport.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Export Data to CSV");
            chooser.setSelectedFile(new java.io.File("data_analyst_export.csv"));

            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                boolean ok = TableExportUtil.exportToCsv(
                        table,
                        chooser.getSelectedFile().getAbsolutePath()
                );
                JOptionPane.showMessageDialog(
                        this,
                        ok ? "Export successful!" : "Export failed.",
                        "Export",
                        ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE
                );
            }
        });

        String[] cols = { "Record ID", "Category", "Value", "Status" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
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

        scrollPane = new JScrollPane(table);
        scrollPane.setBackground(bgSecondary);
        scrollPane.getViewport().setBackground(bgSecondary);
        scrollPane.setBorder(BorderFactory.createLineBorder(borderColor, 1));

        JPanel mainContent = new JPanel(new BorderLayout(0, 0));
        mainContent.setBackground(bgPrimary);
        mainContent.add(toolbar,     BorderLayout.NORTH);
        mainContent.add(scrollPane,  BorderLayout.CENTER);

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
        updateHeader();
        loadData();
    }

    /**
     * Loads mock analytics data into the table and updates stat cards.
     * TODO: replace with real PersistenceService calls.
     */
    protected void loadData() {
        DefaultTableModel m = (DefaultTableModel) table.getModel();
        m.setRowCount(0);
        m.addRow(new Object[]{ "REC-001", "Engagement", "87%", "OK" });
        m.addRow(new Object[]{ "REC-002", "Conversion",  "12%", "Flagged" });
        m.addRow(new Object[]{ "REC-003", "Traffic",     "14k", "OK" });
        m.addRow(new Object[]{ "REC-004", "Retention",   "63%", "OK" });

        setStatValue(0, "4");
        setStatValue(1, "1.8s");
        setStatValue(2, "1");
    }

    // ── Header ────────────────────────────────────────────────────────────────

    /**
     * Updates the subtitle to reflect the current user's enterprise from the JWT session.
     */
    private void updateHeader() {
        subtitleLabel.setText("Enterprise: " + SessionManager.getEnterpriseId());
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