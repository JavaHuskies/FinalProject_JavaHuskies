// ReportingPanel.java
package ui.panels;

import service.SessionManager;
import service.ThemeService;
import ui.ApplicationFrame;
import ui.util.TableExportUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ReportingPanel extends WorkAreaTemplate {

    private static final Color bgPrimary     = ThemeService.colorBgPrimary;
    private static final Color bgSecondary   = ThemeService.colorBgSecondary;
    private static final Color bgTertiary    = ThemeService.colorBgTertiary;
    private static final Color textPrimary   = ThemeService.colorTextPrimary;
    private static final Color textSecondary = ThemeService.colorTextSecondary;
    private static final Color textMuted     = ThemeService.colorTextMuted;
    private static final Color borderColor   = ThemeService.colorBorder;

    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JPanel statsRow;

    private JTable kpiTable;
    private JTable trendTable;
    private JTable rawTable;

    private JTabbedPane tabs;

    public ReportingPanel(ApplicationFrame frame) {
        super(frame);
        setLayout(new BorderLayout());
        buildComponents();
    }

    @Override
    public void onShow() {
        updateHeader();
        loadData();
    }

    private void buildComponents() {

        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        wrapper.setBackground(bgPrimary);
        wrapper.setBorder(new EmptyBorder(32, 80, 24, 80));

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

        statsRow = new JPanel(new GridLayout(1, 3, 12, 0));
        statsRow.setBackground(bgPrimary);
        statsRow.setBorder(new EmptyBorder(0, 0, 20, 0));
        statsRow.add(buildStatCard("Total Work Requests", "—"));
        statsRow.add(buildStatCard("Avg. Resolution Time", "—"));
        statsRow.add(buildStatCard("Active Issues", "—"));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(bgPrimary);
        toolbar.setBorder(new EmptyBorder(0, 0, 12, 0));

        JButton btnExport = buildToolbarButton("Export");
        JButton btnRefresh = buildToolbarButton("Refresh");

        toolbar.add(btnExport);
        toolbar.add(btnRefresh);

        btnRefresh.addActionListener(e -> loadData());

        btnExport.addActionListener(e -> {

            String[] options = { "KPIs", "Trends", "Raw Data" };
            String choice = (String) JOptionPane.showInputDialog(
                    this,
                    "Select dataset to export:",
                    "Export",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == null) return;

            JTable selectedTable = switch (choice) {
                case "KPIs" -> kpiTable;
                case "Trends" -> trendTable;
                case "Raw Data" -> rawTable;
                default -> null;
            };

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Export " + choice + " to CSV");
            chooser.setSelectedFile(new java.io.File(
                    choice.toLowerCase().replace(" ", "_") + ".csv"
            ));

            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                boolean ok = TableExportUtil.exportToCsv(
                        selectedTable,
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

        tabs = new JTabbedPane();
        tabs.setBackground(bgSecondary);
        tabs.setForeground(textMuted);
        tabs.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        tabs.setBorder(BorderFactory.createLineBorder(borderColor, 1));

        tabs.addTab("KPIs", buildKpiTab());
        tabs.addTab("Trends", buildTrendTab());
        tabs.addTab("Raw Data", buildRawTab());

        JPanel mainContent = new JPanel(new BorderLayout(0, 0));
        mainContent.setBackground(bgPrimary);
        mainContent.add(toolbar, BorderLayout.NORTH);
        mainContent.add(tabs, BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout(0, 0));
        top.setBackground(bgPrimary);
        top.add(header, BorderLayout.NORTH);
        top.add(statsRow, BorderLayout.SOUTH);

        wrapper.add(top, BorderLayout.NORTH);
        wrapper.add(mainContent, BorderLayout.CENTER);

        add(wrapper, BorderLayout.CENTER);
    }

    private JScrollPane buildKpiTab() {
        String[] cols = { "Metric", "Value", "Change" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        kpiTable = styledTable(model);
        return styledScroll(kpiTable);
    }

    private JScrollPane buildTrendTab() {
        String[] cols = { "Date", "Requests", "Resolved", "Avg Time" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        trendTable = styledTable(model);
        return styledScroll(trendTable);
    }

    private JScrollPane buildRawTab() {
        String[] cols = { "ID", "Title", "From", "To", "Type", "Status" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        rawTable = styledTable(model);
        return styledScroll(rawTable);
    }

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
