package ui.panels;

import model.Claims;
import service.SessionManager;
import service.ThemeService;
import ui.ApplicationFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EnterpriseAdminPanel extends JPanel {

    private static final Color bgPrimary   = ThemeService.colorBgPrimary;
    private static final Color bgSecondary = ThemeService.colorBgSecondary;
    private static final Color bgTertiary  = ThemeService.colorBgTertiary;
    private static final Color textPrimary = ThemeService.colorTextPrimary;
    private static final Color textMuted   = ThemeService.colorTextMuted;
    private static final Color borderColor = ThemeService.colorBorder;

    private static final String mockTip = "Mock data — replace with PersistenceService query";

    private final ApplicationFrame frame;

    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JPanel statsRow;
    private JTabbedPane tabs;

    private JTable orgTable;
    private JTable userTable;
    private JTable workRequestTable;

    public EnterpriseAdminPanel(ApplicationFrame frame) {
        this.frame = frame;
        setBackground(bgPrimary);
        setLayout(new BorderLayout(0, 0));
        buildComponents();
    }

    public void onShow() {
        if (!SessionManager.guardAny(
                Claims.roleEnterpriseAdmin, Claims.roleEntPresident, Claims.roleEntCoo,
                Claims.roleNetworkAdmin,    Claims.roleSystemAdmin,
                Claims.roleGroupCeo,        Claims.roleGroupCfo)) {
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

        titleLabel = new JLabel("Enterprise Admin");
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
        statsRow.add(buildStatCard("Organizations", "—"));
        statsRow.add(buildStatCard("Users", "—"));
        statsRow.add(buildStatCard("Work Requests", "—"));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(bgPrimary);
        toolbar.setBorder(new EmptyBorder(0, 0, 12, 0));
        toolbar.add(buildToolbarButton("New Org",  e -> onNewOrg()));
        toolbar.add(buildToolbarButton("New User", e -> onNewUser()));
        toolbar.add(buildToolbarButton("Export",   e -> onExport()));

        tabs = new JTabbedPane();
        tabs.setBackground(bgSecondary);
        tabs.setForeground(textMuted);
        tabs.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        tabs.setBorder(BorderFactory.createLineBorder(borderColor, 1));

        tabs.addTab("Organizations", buildOrgTab());
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

    private JScrollPane buildOrgTab() {
        String[] cols = { "Organization", "ID", "Users", "Work Requests" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        orgTable = styledTable(model);
        JScrollPane sp = styledScroll(orgTable);
        sp.setToolTipText(mockTip);
        return sp;
    }

    private JScrollPane buildUserTab() {
        String[] cols = { "Username", "Role", "Organization", "Email" };
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
        String eid = SessionManager.getEnterpriseId();
        loadOrgs(eid);
        loadUsers(eid);
        loadWorkRequests(eid);
        updateStats();
    }

    private void loadOrgs(String enterpriseId) {
        DefaultTableModel m = (DefaultTableModel) orgTable.getModel();
        m.setRowCount(0);

        m.addRow(new Object[]{ "Slartibartfast Pictures", "slartibartfastPictures", "4", "3" });
        m.addRow(new Object[]{ "Bistromath Animation",    "bistromathAnimation",    "4", "1" });
    }

    private void loadUsers(String enterpriseId) {
        DefaultTableModel m = (DefaultTableModel) userTable.getModel();
        m.setRowCount(0);

        m.addRow(new Object[]{ "netadmin",   "Network Admin", "slartibartfastPictures", "netadmin@deepthought.com" });
        m.addRow(new Object[]{ "creative1",  "Creative Lead", "bistromathAnimation",    "creative1@deepthought.com" });
    }

    private void loadWorkRequests(String enterpriseId) {
        DefaultTableModel m = (DefaultTableModel) workRequestTable.getModel();
        m.setRowCount(0);

        m.addRow(new Object[]{ "WR-01", "Galactic Odyssey Release", "Slartibartfast Pictures", "Megadodo Licensing", "Licensing", "Pending" });
        m.addRow(new Object[]{ "WR-02", "Park Theming — Vogon World", "Magrathea Studios", "Magrathea Theme Worlds", "Content", "In Review" });
    }

    private void updateStats() {
        setStatValue(0, String.valueOf(orgTable.getRowCount()));
        setStatValue(1, String.valueOf(userTable.getRowCount()));
        setStatValue(2, String.valueOf(workRequestTable.getRowCount()));
    }

    private void updateHeader() {
        subtitleLabel.setText("Enterprise: " + SessionManager.getEnterpriseId());
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
        table.setForeground(textPrimary);
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
