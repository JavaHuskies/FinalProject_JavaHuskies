package ui.panels;

import model.Claims;
import service.SessionManager;
import service.ThemeService;
import ui.ApplicationFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class NetworkAdminPanel extends JPanel {

    private static final Color bgPrimary     = ThemeService.colorBgPrimary;
    private static final Color bgSecondary   = ThemeService.colorBgSecondary;
    private static final Color bgTertiary    = ThemeService.colorBgTertiary;
    private static final Color textPrimary   = ThemeService.colorTextPrimary;
    private static final Color textSecondary = ThemeService.colorTextSecondary;
    private static final Color textMuted     = ThemeService.colorTextMuted;
    private static final Color borderColor   = ThemeService.colorBorder;

    private static final String mockTip = "Mock data — replace with PersistenceService query";

    private final ApplicationFrame frame;

    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JPanel statsRow;
    private JTabbedPane tabs;

    private JTable enterpriseTable;
    private JTable userTable;
    private JTable workRequestTable;

    public NetworkAdminPanel(ApplicationFrame frame) {
        this.frame = frame;
        setBackground(bgPrimary);
        setLayout(new BorderLayout(0, 0));
        buildComponents();
    }

    public void onShow() {
        if (!SessionManager.guardAny(
                Claims.roleNetworkAdmin, Claims.roleSystemAdmin,
                Claims.roleGroupCeo,     Claims.roleGroupCfo)) {
            frame.showPanel(ApplicationFrame.panelStaffLogin);
            return;
        }
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

        titleLabel = new JLabel("Network Admin");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        titleLabel.setForeground(textPrimary);

        subtitleLabel = new JLabel("Deep Thought Entertainment Group");
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        subtitleLabel.setForeground(textMuted);

        header.add(titleLabel, BorderLayout.NORTH);
        header.add(subtitleLabel, BorderLayout.SOUTH);

        statsRow = new JPanel(new GridLayout(1, 4, 12, 0));
        statsRow.setBackground(bgPrimary);
        statsRow.setBorder(new EmptyBorder(0, 0, 20, 0));
        statsRow.add(buildStatCard("Enterprises", "—"));
        statsRow.add(buildStatCard("Organizations", "—"));
        statsRow.add(buildStatCard("Users", "—"));
        statsRow.add(buildStatCard("Work Requests", "—"));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(bgPrimary);
        toolbar.setBorder(new EmptyBorder(0, 0, 12, 0));
        toolbar.add(buildToolbarButton("New Enterprise", e -> onNewEnterprise()));
        toolbar.add(buildToolbarButton("New Org", e -> onNewOrg()));
        toolbar.add(buildToolbarButton("New User", e -> onNewUser()));
        toolbar.add(buildToolbarButton("Export", e -> onExport()));

        tabs = new JTabbedPane();
        tabs.setBackground(bgSecondary);
        tabs.setForeground(textMuted);
        tabs.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        tabs.setBorder(BorderFactory.createLineBorder(borderColor, 1));

        tabs.addTab("Enterprises", buildEnterpriseTab());
        tabs.addTab("Users", buildUserTab());
        tabs.addTab("Work Requests", buildWorkRequestTab());

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

    private JScrollPane buildEnterpriseTab() {
        String[] cols = { "Enterprise", "ID", "Orgs", "Users", "Accent" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        enterpriseTable = styledTable(model);
        JScrollPane sp = styledScroll(enterpriseTable);
        sp.setToolTipText(mockTip);
        return sp;
    }

    private JScrollPane buildUserTab() {
        String[] cols = { "Username", "Role", "Enterprise", "Organization", "Email" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        userTable = styledTable(model);
        JScrollPane sp = styledScroll(userTable);
        sp.setToolTipText(mockTip);
        return sp;
    }

    private JScrollPane buildWorkRequestTab() {
        String[] cols = { "ID", "Title", "From", "To", "Type", "Status" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        workRequestTable = styledTable(model);
        JScrollPane sp = styledScroll(workRequestTable);
        sp.setToolTipText(mockTip);
        return sp;
    }

    private void loadData() {
        loadEnterprises();
        loadUsers();
        loadWorkRequests();
        updateStats();
    }

    private void loadEnterprises() {
        DefaultTableModel m = (DefaultTableModel) enterpriseTable.getModel();
        m.setRowCount(0);

        m.addRow(new Object[]{ "Magrathea Studios",        "magratheaStudios",       "2", "8", "Purple" });
        m.addRow(new Object[]{ "Starship Titanic Leisure", "starshipTitanicLeisure", "2", "6", "Coral" });
        m.addRow(new Object[]{ "Galactic Broadcasting",    "galacticBroadcasting",   "2", "5", "Blue" });
        m.addRow(new Object[]{ "Sirius Cybernetics",       "siriusCybernetics",      "2", "5", "Teal" });
    }

    private void loadUsers() {
        DefaultTableModel m = (DefaultTableModel) userTable.getModel();
        m.setRowCount(0);

        m.addRow(new Object[]{ "netadmin",  "Network Admin",      "magratheaStudios",       "slartibartfastPictures",        "netadmin@deepthought.com" });
        m.addRow(new Object[]{ "entadmin",  "Enterprise Admin",   "starshipTitanicLeisure", "magratheaThemeWorlds",          "entadmin@deepthought.com" });
        m.addRow(new Object[]{ "grpceo",    "Group CEO",          "magratheaStudios",       "slartibartfastPictures",        "grpceo@deepthought.com" });
        m.addRow(new Object[]{ "orgdir1",   "Org Director",       "galacticBroadcasting",   "infiniteImprobabilityStreaming","orgdir1@deepthought.com" });
        m.addRow(new Object[]{ "creative1", "Creative Lead",      "magratheaStudios",       "bistromathAnimation",           "creative1@deepthought.com" });
        m.addRow(new Object[]{ "tech1",     "Technology Lead",    "siriusCybernetics",      "megadodoLicensing",             "tech1@deepthought.com" });
        m.addRow(new Object[]{ "mktg1",     "Marketing Lead",     "galacticBroadcasting",   "panGalacticBroadcast",          "mktg1@deepthought.com" });
        m.addRow(new Object[]{ "comply1",   "Compliance Officer", "starshipTitanicLeisure", "milliwaysEntertainment",        "comply1@deepthought.com" });
        m.addRow(new Object[]{ "analyst1",  "Data Analyst",       "siriusCybernetics",      "hooloovooRetail",               "analyst1@deepthought.com" });
    }

    private void loadWorkRequests() {
        DefaultTableModel m = (DefaultTableModel) workRequestTable.getModel();
        m.setRowCount(0);

        m.addRow(new Object[]{ "WR-01", "Galactic Odyssey Release",    "Slartibartfast Pictures",         "Megadodo Licensing",      "Licensing",  "Pending" });
        m.addRow(new Object[]{ "WR-02", "Park Theming — Vogon World",  "Magrathea Studios",               "Magrathea Theme Worlds",  "Content",    "In Review" });
        m.addRow(new Object[]{ "WR-03", "Streaming Premiere Assets",   "Magrathea Theme Worlds",          "Pan-Galactic Broadcast",  "Broadcast",  "Active" });
        m.addRow(new Object[]{ "WR-04", "Retail Campaign Q3",          "Hooloovoo Retail",                "Pan-Galactic Broadcast",  "Marketing",  "Pending" });
        m.addRow(new Object[]{ "WR-05", "Theme World Event Package",   "Magrathea Theme Worlds",          "Milliways Entertainment", "Events",     "Active" });
        m.addRow(new Object[]{ "WR-06", "Animation Commission",        "Infinite Improbability Streaming","Bistromath Animation",    "Content",    "In Review" });
        m.addRow(new Object[]{ "WR-07", "Licensing Agreement Renewal", "Megadodo Licensing",              "Hooloovoo Retail",        "Licensing",  "Closed" });
        m.addRow(new Object[]{ "WR-08", "Guest Complaint Escalation",  "Milliways Entertainment",         "Compliance Officer",      "Compliance", "Pending" });
    }

    private void updateStats() {
        setStatValue(0, String.valueOf(enterpriseTable.getRowCount()));
        setStatValue(1, "8");
        setStatValue(2, String.valueOf(userTable.getRowCount()));
        setStatValue(3, String.valueOf(workRequestTable.getRowCount()));
    }

    private void updateHeader() {
        subtitleLabel.setText("Deep Thought Entertainment Group");
    }

    private void onNewEnterprise() {
        JOptionPane.showMessageDialog(this, "New enterprise form — coming soon.");
    }

    private void onNewOrg() {
        JOptionPane.showMessageDialog(this, "New organization form — coming soon.");
    }

    private void onNewUser() {
        JOptionPane.showMessageDialog(this, "New user form — coming soon.");
    }

    private void onExport() {
        JOptionPane.showMessageDialog(this, "Export — coming soon.");
    }

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
        gbc.gridx = 0;

        JLabel labelLbl = new JLabel(label);
        labelLbl.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        labelLbl.setForeground(textMuted);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
        valueLbl.setForeground(textPrimary);

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 4, 0);
        card.add(labelLbl, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(valueLbl, gbc);

        return card;
    }

    private JButton buildToolbarButton(String label, java.awt.event.ActionListener action) {
        JButton btn = new JButton(label);
        btn.setBackground(bgTertiary);
        btn.setForeground(textMuted);
        btn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                new EmptyBorder(6, 14, 6, 14)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(bgSecondary); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(bgTertiary); }
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
