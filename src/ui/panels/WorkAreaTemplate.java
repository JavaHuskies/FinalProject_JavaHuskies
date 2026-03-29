
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
 * ============================================================
 * WORK AREA TEMPLATE — copy and rename this file to create
 * a new work area panel.
 *
 * HOW TO USE:
 *   1. Copy this file, rename it (e.g. OrgDirectorPanel.java)
 *   2. Rename the class to match the filename
 *   3. Update requiredRole to the correct Claims.role* constant
 *   4. Update pageTitle and pageSubtitle
 *   5. Replace stat card labels and values with real data
 *   6. Replace table columns with real columns
 *   7. Replace toolbar buttons with real actions
 *   8. Open in NetBeans GUI Builder (Matisse) to add components
 *      to the mainContent panel as needed
 *   9. Uncomment register() in ApplicationFrame.initPanels()
 *
 * BACKGROUND COLOR for all GUI Builder components: new java.awt.Color(10, 10, 26)
 * ============================================================
 */
public class WorkAreaTemplate extends JPanel {

    // ── Configuration — update these when copying ─────────────────────────────
    private static final String requiredRole  = Claims.roleOrgDirector; // change per panel
    private static final String pageTitle     = "Work Area Title";      // change per panel
    private static final String pageSubtitle  = "Organization name";    // change per panel

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

    // ── UI components — add more as needed ────────────────────────────────────
    private JLabel      titleLabel;
    private JLabel      subtitleLabel;
    private JPanel      statsRow;
    private JPanel      toolbar;
    private JTable      dataTable;
    private JScrollPane tableScroll;
    private JPanel      mainContent;

    // ─────────────────────────────────────────────────────────────────────────

	/**
	 * Constructs the work area panel and builds all UI components.
	 * Copy this file and update requiredRole, pageTitle, and pageSubtitle.
	 *
	 * @param frame the parent ApplicationFrame used for panel navigation
	 */
    public WorkAreaTemplate(ApplicationFrame frame) {
        this.frame = frame;
        setBackground(new java.awt.Color(10, 10, 26));
        setLayout(new BorderLayout(0, 0));
        buildComponents();
    }

    // ── Utility — defined first so it can be used anywhere below ─────────────

    /** Splits a camelCase string into readable display text. */
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

    // ── Build ─────────────────────────────────────────────────────────────────

    /**
     * Builds all UI components. Named buildComponents() to avoid
     * conflict with NetBeans' reserved initComponents() method.
     */
    private void buildComponents() {
        // ── Outer padding wrapper ─────────────────────────────────────────
        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        wrapper.setBackground(bgPrimary);
        wrapper.setBorder(new EmptyBorder(24, 28, 24, 28));

        // ── Header row: title + subtitle ──────────────────────────────────
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

        // ── Stats row — three cards ───────────────────────────────────────
        statsRow = new JPanel(new GridLayout(1, 3, 12, 0));
        statsRow.setBackground(bgPrimary);
        statsRow.setBorder(new EmptyBorder(0, 0, 16, 0));
        statsRow.add(buildStatCard("Stat One",   "—"));
        statsRow.add(buildStatCard("Stat Two",   "—"));
        statsRow.add(buildStatCard("Stat Three", "—"));

        // ── Toolbar — action buttons ──────────────────────────────────────
        toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(bgPrimary);
        toolbar.setBorder(new EmptyBorder(0, 0, 12, 0));
        toolbar.add(buildToolbarButton("+ New"));
        toolbar.add(buildToolbarButton("Export"));

        // ── Data table ────────────────────────────────────────────────────
        String[] columns = { "Column 1", "Column 2", "Column 3", "Status" };
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

        // ── Main content — GUI Builder adds components here ───────────────
        mainContent = new JPanel(new BorderLayout(0, 0));
        mainContent.setBackground(bgPrimary);
        mainContent.add(toolbar,     BorderLayout.NORTH);
        mainContent.add(tableScroll, BorderLayout.CENTER);

        // ── Assemble ──────────────────────────────────────────────────────
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
     * Guards the session, then loads data.
     * Add your data loading logic in loadData().
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
     * Load or refresh panel data here.
     * Called from onShow() and any refresh action.
     * TODO: replace stub content with real PersistenceService calls (Anan).
     */
    private void loadData() {
        DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
        model.setRowCount(0);
        // TODO: query PersistenceService and populate rows
        // Example:
        // for (WorkRequest wr : PersistenceService.getInstance().getWorkRequests(...)) {
        //     model.addRow(new Object[]{ wr.getId(), wr.getTitle(), wr.getTarget(), wr.getStatus() });
        // }
    }

    /**
     * Updates the subtitle to reflect the current user's org.
     * Called on show — org comes from the active JWT session.
     */
    private void updateSubtitle() {
        String org = SessionManager.getOrgId();
        subtitleLabel.setText(org != null && !org.isBlank()
            ? formatCamelCase(org) : pageSubtitle);
    }

    // ── Stat card helpers ─────────────────────────────────────────────────────

    /**
     * Updates a stat card value by index (0, 1, 2).
     * Call from loadData() once real values are available.
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
	 * @param label display label (e.g. "Stat One")
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
	 * Wire an ActionListener after calling this method.
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}