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
 * Reporting dashboard — accessible by dataAnalyst, enterpriseAdmin,
 * networkAdmin, and systemAdmin roles.
 *
 * Layout: stats row + toolbar + tabbed pane (KPIs | Trends | Raw Data)
 */
public class ReportingPanel extends JPanel {

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

    private JTable kpiTable;
    private JTable trendTable;
    private JTable rawTable;

    public ReportingPanel(ApplicationFrame frame) {
        this.frame = frame;
        setBackground(bgPrimary);
        setLayout(new BorderLayout(0, 0));
        buildComponents();
    }

    // Lifecycle
    public void onShow() {
        if (!SessionManager.guardAny(
                Claims.roleDataAnalyst,
                Claims.roleEnterpriseAdmin,
                Claims.roleNetworkAdmin,
                Claims.roleSystemAdmin)) {
            frame.showPanel(ApplicationFrame.panelStaffLogin);
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

        titleLabel = new JLabel("Reporting Dashboard");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        titleLabel.setForeground(textPrimary);

        subtitleLabel = new JLabel("—");
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        subtitleLabel.setForeground(textMuted);

        header.add(titleLabel, BorderLayout.NORTH);
        header.add(subtitleLabel, BorderLayout.SOUTH);

        // Stats row
        statsRow = new JPanel(new GridLayout(1, 3, 12, 0));
        statsRow.setBackground(bgPrimary);
        statsRow.setBorder(new EmptyBorder(0, 0, 20, 0));
        statsRow.add(buildStatCard("Total Work Requests", "—"));
        statsRow.add(buildStatCard("Avg. Resolution Time", "—"));
        statsRow.add(buildStatCard("Active Issues", "—"));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(bgPrimary);
        toolbar.setBorder(new EmptyBorder(0, 0, 12, 0));
        toolbar.add(buildToolbarButton("Export"));
        toolbar.add(buildToolbarButton("Refresh"));

        // Tabs
        tabs = new JTabbedPane();
        tabs.setBackground(bgSecondary);
        tabs.setForeground(textMuted);
        tabs.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        tabs.setBorder(BorderFactory.createLineBorder(borderColor, 1));

        tabs.addTab("KPIs", buildKpiTab());
        tabs.addTab("Trends", buildTrendTab());
        tabs.addTab("Raw Data", buildRawTab());

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
    private JScrollPane buildKpiTab() {
        String[] cols = { "Metric", "Value", "Change" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        kpiTable = styledTable(model);
        JScrollPane sp = styledScroll(kpiTable);
        sp.setToolTipText(mockTip);
        return sp;
    }

    private JScrollPane buildTrendTab() {
        String[] cols = { "Date", "Requests", "Resolved", "Avg Time" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        trendTable = styledTable(model);
        JScrollPane sp = styledScroll(trendTable);
        sp.setToolTipText(mockTip);
        return sp;
    }

    private JScrollPane buildRawTab() {
        String[] cols = { "ID", "Title", "From", "To", "Type", "Status" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        rawTable = styledTable(model);
        JScrollPane sp = styledScroll(rawTable);
        sp.setToolTipText(mockTip);
        return sp;
    }

    // Data loading
    private void loadData() {
        loadKpis();
        loadTrends();
        loadRaw();
        updateStats();
    }

    private void loadKpis() {
        DefaultTableModel m = (DefaultTableModel) kpiTable.getModel();
        m.setRowCount(0);
        m.addRow(new Object[]{ "Total Requests", "42", "+5%" });
        m.addRow(new Object[]{ "Avg Resolution Time", "3.2 days", "-8%" });
        m.addRow(new Object[]{ "Active Issues", "7", "+2" });
    }

    private void loadTrends() {
        DefaultTableModel m = (DefaultTableModel) trendTable.getModel();
        m.setRowCount(0);
        m.addRow(new Object[]{ "2026-03-01", "12", "10", "3.1d" });
        m.addRow(new Object[]{ "2026-03-02", "14", "13", "2.9d" });
        m.addRow(new Object[]{ "2026-03-03", "16", "15", "3.4d" });
    }

    private void loadRaw() {
        DefaultTableModel m = (DefaultTableModel) rawTable.getModel();
        m.setRowCount(0);
        m.addRow(new Object[]{ "WR-01", "Galactic Odyssey Release", "Slartibartfast Pictures", "Megadodo Licensing", "Licensing", "Pending" });
        m.addRow(new Object[]{ "WR-04", "Retail Campaign Q3", "Hooloovoo Retail", "Pan-Galactic Broadcast", "Marketing", "Pending" });
        m.addRow(new Object[]{ "WR-08", "Guest Complaint Escalation", "Milliways Entertainment", "Compliance Officer", "Compliance", "Pending" });
    }

    private void updateStats() {
        setStatValue(0, "42");
        setStatValue(1, "3.2 days");
        setStatValue(2, "7");
    }

    private void updateHeader() {
        subtitleLabel.setText("Enterprise: " + SessionManager.getEnterpriseId());
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
