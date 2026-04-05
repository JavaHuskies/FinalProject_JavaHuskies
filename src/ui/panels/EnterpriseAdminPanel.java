package ui.panels;

import model.Claims;
import service.SessionManager;
import service.ThemeService;
import util.ValidationUtils;
import ui.ApplicationFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.UUID;
import util.ValidationUtils.ValidationResult;

/**
 * Work area panel for Enterprise Admin, Enterprise President, and COO roles.
 * Displays enterprise-scoped organization, user, and work request data.
 * Supports CRUD operations for organizations and users via inline forms.
 */
public class EnterpriseAdminPanel extends JPanel {

    // ── Colors ────────────────────────────────────────────────────────────────
    private static final Color bgPrimary     = ThemeService.colorBgPrimary;
    private static final Color bgSecondary   = ThemeService.colorBgSecondary;
    private static final Color bgTertiary    = ThemeService.colorBgTertiary;
    private static final Color textPrimary   = ThemeService.colorTextPrimary;
    private static final Color textMuted     = ThemeService.colorTextMuted;
    private static final Color borderColor   = ThemeService.colorBorder;

    private static final String mockTip = "Mock data — replace with PersistenceService query";

    // ── Org-level roles only ──────────────────────────────────────────────────
    private static final String[] orgRoles = {
        Claims.roleOrgDirector,
        Claims.roleCreativeLead,
        Claims.roleTechnologyLead,
        Claims.roleMarketingLead,
        Claims.roleComplianceOfficer,
        Claims.roleDataAnalyst
    };

    private static final String[] orgs = {
        "slartibartfastPictures",
        "bistromathAnimation",
        "magratheaThemeWorlds",
        "milliwaysEntertainment",
        "infiniteImprobabilityStreaming",
        "panGalacticBroadcast",
        "megadodoLicensing",
        "hooloovooRetail"
    };

    // ── Frame reference ───────────────────────────────────────────────────────
    private final ApplicationFrame frame;

    // ── UI components ─────────────────────────────────────────────────────────
    private JLabel      titleLabel;
    private JLabel      subtitleLabel;
    private JPanel      statsRow;
    private JTabbedPane tabs;
    private JPanel      mainContent;

    private JTable orgTable;
    private JTable userTable;
    private JTable workRequestTable;

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Constructs the Enterprise Admin work area panel.
     *
     * @param frame the parent ApplicationFrame used for panel navigation
     */
    public EnterpriseAdminPanel(ApplicationFrame frame) {
        this.frame = frame;
        setBackground(bgPrimary);
        setLayout(new BorderLayout(0, 0));
        buildComponents();
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    /**
     * Called by ApplicationFrame.showPanel() when this panel becomes visible.
     * Guards the session then loads data.
     */
    public void onShow() {
        if (!SessionManager.guardAny(
                Claims.roleEnterpriseAdmin,
                Claims.roleEntPresident,
                Claims.roleEntCoo)) {
            frame.showPanel(ApplicationFrame.panelStaffLogin);
            return;
        }
        updateHeader();
        loadData();
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

        titleLabel = new JLabel("Enterprise Admin");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        titleLabel.setForeground(textPrimary);

        subtitleLabel = new JLabel("—");
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        subtitleLabel.setForeground(textMuted);

        header.add(titleLabel,    BorderLayout.NORTH);
        header.add(subtitleLabel, BorderLayout.SOUTH);

        statsRow = new JPanel(new GridLayout(1, 3, 12, 0));
        statsRow.setBackground(bgPrimary);
        statsRow.setBorder(new EmptyBorder(0, 0, 20, 0));
        statsRow.add(buildStatCard("Organizations", "—"));
        statsRow.add(buildStatCard("Users",         "—"));
        statsRow.add(buildStatCard("Work Requests", "—"));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(bgPrimary);
        toolbar.setBorder(new EmptyBorder(0, 0, 12, 0));
        toolbar.add(buildToolbarButton("New Org",  e -> showNewOrgForm()));
        toolbar.add(buildToolbarButton("New User", e -> showNewUserForm()));
        toolbar.add(buildToolbarButton("Edit",     e -> onEditSelected()));
        toolbar.add(buildToolbarButton("Delete",   e -> onDeleteSelected()));
        toolbar.add(buildToolbarButton("Export",   e -> onExport()));

        tabs = new JTabbedPane();
        tabs.setBackground(bgSecondary);
        tabs.setForeground(textMuted);
        tabs.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        tabs.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        tabs.addTab("Organizations", buildOrgTab());
        tabs.addTab("Users",         buildUserTab());
        tabs.addTab("Work Requests", buildWorkRequestTab());

        mainContent = new JPanel(new CardLayout());
        mainContent.setBackground(bgPrimary);

        JPanel tableView = new JPanel(new BorderLayout(0, 0));
        tableView.setBackground(bgPrimary);
        tableView.add(toolbar, BorderLayout.NORTH);
        tableView.add(tabs,    BorderLayout.CENTER);

        mainContent.add(tableView, "tableView");

        JPanel top = new JPanel(new BorderLayout(0, 0));
        top.setBackground(bgPrimary);
        top.add(header,   BorderLayout.NORTH);
        top.add(statsRow, BorderLayout.SOUTH);

        wrapper.add(top,         BorderLayout.NORTH);
        wrapper.add(mainContent, BorderLayout.CENTER);

        add(wrapper, BorderLayout.CENTER);
    }

    // ── Table tabs ────────────────────────────────────────────────────────────

    private JScrollPane buildOrgTab() {
        String[] cols = { "Organization", "ID", "Users", "Work Requests" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        orgTable = styledTable(model);
        orgTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
        workRequestTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane sp = styledScroll(workRequestTable);
        sp.setToolTipText(mockTip);
        return sp;
    }

    // ── Data loading ──────────────────────────────────────────────────────────

    /**
     * Loads mock data scoped to the current user's enterprise.
     * TODO: replace with real PersistenceService queries scoped by enterpriseId.
     */
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
        m.addRow(new Object[]{ "netadmin",  "networkAdmin", "slartibartfastPictures", "netadmin@deepthought.com"  });
        m.addRow(new Object[]{ "creative1", "creativeLead", "bistromathAnimation",    "creative1@deepthought.com" });
    }

    private void loadWorkRequests(String enterpriseId) {
        DefaultTableModel m = (DefaultTableModel) workRequestTable.getModel();
        m.setRowCount(0);
        m.addRow(new Object[]{ "WR-01", "Galactic Odyssey Release",   "Slartibartfast Pictures", "Megadodo Licensing",     "Licensing", "Pending"   });
        m.addRow(new Object[]{ "WR-02", "Park Theming — Vogon World", "Magrathea Studios",       "Magrathea Theme Worlds", "Content",   "In Review" });
    }

    private void updateStats() {
        setStatValue(0, String.valueOf(orgTable.getRowCount()));
        setStatValue(1, String.valueOf(userTable.getRowCount()));
        setStatValue(2, String.valueOf(workRequestTable.getRowCount()));
    }

    private void updateHeader() {
        subtitleLabel.setText("Enterprise: " + SessionManager.getEnterpriseId());
    }

    // ── Edit / Delete dispatch ────────────────────────────────────────────────

    /**
     * Dispatches Edit action based on the active tab.
     * Prompts the user to select a row if none is selected.
     * Work Requests tab does not support edit.
     */
    private void onEditSelected() {
        int tabIdx = tabs.getSelectedIndex();
        switch (tabIdx) {
            case 0 -> {
                int row = orgTable.getSelectedRow();
                if (row < 0) { showNoSelectionPrompt("organization"); return; }
                showEditOrgForm(row);
            }
            case 1 -> {
                int row = userTable.getSelectedRow();
                if (row < 0) { showNoSelectionPrompt("user"); return; }
                showEditUserForm(row);
            }
            case 2 -> JOptionPane.showMessageDialog(this,
                "Work requests cannot be edited here.",
                "Not Available", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Dispatches Delete action based on the active tab.
     * Prompts the user to select a row if none is selected.
     * Requires confirmation before deleting.
     */
    private void onDeleteSelected() {
        int tabIdx = tabs.getSelectedIndex();
        switch (tabIdx) {
            case 0 -> {
                int row = orgTable.getSelectedRow();
                if (row < 0) { showNoSelectionPrompt("organization"); return; }
                String name = (String) orgTable.getValueAt(row, 0);
                String id   = (String) orgTable.getValueAt(row, 1);
                if (confirmDelete("organization", name)) {
                    // TODO: PersistenceService.getInstance().deleteOrganization(id);
                    ((DefaultTableModel) orgTable.getModel()).removeRow(row);
                    showSuccess("Organization '" + name + "' deleted (pending PersistenceService).");
                    updateStats();
                }
            }
            case 1 -> {
                int row = userTable.getSelectedRow();
                if (row < 0) { showNoSelectionPrompt("user"); return; }
                String username = (String) userTable.getValueAt(row, 0);
                if (confirmDelete("user", username)) {
                    // TODO: PersistenceService.getInstance().deleteUser(username);
                    ((DefaultTableModel) userTable.getModel()).removeRow(row);
                    showSuccess("User '" + username + "' deleted (pending PersistenceService).");
                    updateStats();
                }
            }
            case 2 -> {
                int row = workRequestTable.getSelectedRow();
                if (row < 0) { showNoSelectionPrompt("work request"); return; }
                String wrId    = (String) workRequestTable.getValueAt(row, 0);
                String wrTitle = (String) workRequestTable.getValueAt(row, 1);
                if (confirmDelete("work request", wrTitle)) {
                    // TODO: PersistenceService.getInstance().deleteWorkRequest(wrId);
                    ((DefaultTableModel) workRequestTable.getModel()).removeRow(row);
                    showSuccess("Work request '" + wrId + "' deleted (pending PersistenceService).");
                    updateStats();
                }
            }
        }
    }

    // ── CRUD — Create forms ───────────────────────────────────────────────────

    /**
     * Renders the New Organization form in the main content area.
     * Organization is scoped to the current user's enterprise.
     */
    private void showNewOrgForm() {
        JPanel form = buildFormPanel("New Organization");

        JTextField nameField = styledField();
        JTextField idField   = styledField();
        idField.setToolTipText("camelCase — e.g. vogonOperations");

        form.add(fieldRow("Organization Name", nameField));
        form.add(fieldRow("Organization ID",   idField));
        form.add(buildFormButtons(
            () -> {
                String name = nameField.getText().trim();
                String id   = idField.getText().trim();
                String eid  = SessionManager.getEnterpriseId();

                if (name.isEmpty() || id.isEmpty()) {
                    showFormError(form, "All fields are required.");
                    return;
                }
                ValidationResult idCheck = ValidationUtils.requireNonBlank(id, "Organization ID");
                if (!idCheck.valid) { showFormError(form, idCheck.message); return; }
                if (id.contains(" ")) { showFormError(form, "Organization ID must have no spaces."); return; }

                // TODO: PersistenceService.getInstance().saveOrganization(new Organization(id, name, eid));
                showSuccess("Organization '" + name + "' created (pending PersistenceService).");
                showTableView();
                loadData();
            },
            this::showTableView
        ));

        showForm(form);
    }

    /**
     * Renders the New User form in the main content area.
     * Role selection is limited to org-level roles.
     */
    private void showNewUserForm() {
        JPanel form = buildFormPanel("New User");

        JTextField        usernameField = styledField();
        JTextField        emailField    = styledField();
        JPasswordField    passwordField = new JPasswordField();
        stylePasswordField(passwordField);
        JComboBox<String> roleBox       = styledCombo(orgRoles);
        JComboBox<String> orgBox        = styledCombo(orgs);

        form.add(fieldRow("Username",     usernameField));
        form.add(fieldRow("Email",        emailField));
        form.add(fieldRow("Password",     passwordField));
        form.add(fieldRow("Role",         roleBox));
        form.add(fieldRow("Organization", orgBox));
        form.add(buildFormButtons(
            () -> {
                String username = usernameField.getText().trim();
                String email    = emailField.getText().trim();
                String password = new String(passwordField.getPassword());
                String role     = (String) roleBox.getSelectedItem();
                String oid      = (String) orgBox.getSelectedItem();
                String eid      = SessionManager.getEnterpriseId();

                if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    showFormError(form, "Username, email, and password are required.");
                    return;
                }
                ValidationResult unCheck = ValidationUtils.requireUsername(username);
                if (!unCheck.valid) { showFormError(form, unCheck.message); return; }
                ValidationResult emCheck = ValidationUtils.requireEmail(email);
                if (!emCheck.valid) { showFormError(form, emCheck.message); return; }
                ValidationResult pwCheck = ValidationUtils.requireValidPassword(password);
                if (!pwCheck.valid) { showFormError(form, pwCheck.message); return; }

                String userId = UUID.randomUUID().toString();
                String pwHash = service.AuthService.getInstance().hashPassword(password);

                // TODO: PersistenceService.getInstance().saveUser(
                //     new User(userId, username, pwHash, role, oid, eid, email));
                showSuccess("User '" + username + "' created (pending PersistenceService).");
                showTableView();
                loadData();
            },
            this::showTableView
        ));

        showForm(form);
    }

    // ── CRUD — Edit forms ─────────────────────────────────────────────────────

    /**
     * Renders the Edit Organization form pre-populated from the selected table row.
     * Enterprise is implicit from the current session.
     *
     * @param row selected row index in orgTable
     */
    private void showEditOrgForm(int row) {
        String existingName = (String) orgTable.getValueAt(row, 0);
        String existingId   = (String) orgTable.getValueAt(row, 1);

        JPanel form = buildFormPanel("Edit Organization");

        JTextField nameField = styledField();
        JTextField idField   = styledField();
        nameField.setText(existingName);
        idField.setText(existingId);
        idField.setToolTipText("camelCase — e.g. vogonOperations");

        form.add(fieldRow("Organization Name", nameField));
        form.add(fieldRow("Organization ID",   idField));
        form.add(buildFormButtons(
            () -> {
                String name = nameField.getText().trim();
                String id   = idField.getText().trim();
                String eid  = SessionManager.getEnterpriseId();

                if (name.isEmpty() || id.isEmpty()) {
                    showFormError(form, "All fields are required.");
                    return;
                }
                ValidationResult idCheck = ValidationUtils.requireNonBlank(id, "Organization ID");
                if (!idCheck.valid) { showFormError(form, idCheck.message); return; }
                if (id.contains(" ")) { showFormError(form, "Organization ID must have no spaces."); return; }

                // TODO: PersistenceService.getInstance().updateOrganization(existingId, new Organization(id, name, eid));
                showSuccess("Organization '" + name + "' updated (pending PersistenceService).");
                showTableView();
                loadData();
            },
            this::showTableView
        ));

        showForm(form);
    }

    /**
     * Renders the Edit User form pre-populated from the selected table row.
     * Password is not editable here — use a separate reset flow.
     * Enterprise is implicit from the current session.
     *
     * @param row selected row index in userTable
     */
    private void showEditUserForm(int row) {
        String existingUsername = (String) userTable.getValueAt(row, 0);
        String existingRole     = (String) userTable.getValueAt(row, 1);
        String existingOrg      = (String) userTable.getValueAt(row, 2);
        String existingEmail    = (String) userTable.getValueAt(row, 3);

        JPanel form = buildFormPanel("Edit User");

        JTextField        usernameField = styledField();
        JTextField        emailField    = styledField();
        JComboBox<String> roleBox       = styledCombo(orgRoles);
        JComboBox<String> orgBox        = styledCombo(orgs);

        usernameField.setText(existingUsername);
        emailField.setText(existingEmail);
        roleBox.setSelectedItem(existingRole);
        orgBox.setSelectedItem(existingOrg);

        JLabel passwordNote = new JLabel("Password is not editable here.");
        passwordNote.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
        passwordNote.setForeground(textMuted);
        passwordNote.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordNote.setBorder(new EmptyBorder(0, 0, 12, 0));

        form.add(fieldRow("Username",     usernameField));
        form.add(fieldRow("Email",        emailField));
        form.add(passwordNote);
        form.add(fieldRow("Role",         roleBox));
        form.add(fieldRow("Organization", orgBox));
        form.add(buildFormButtons(
            () -> {
                String username = usernameField.getText().trim();
                String email    = emailField.getText().trim();
                String role     = (String) roleBox.getSelectedItem();
                String oid      = (String) orgBox.getSelectedItem();
                String eid      = SessionManager.getEnterpriseId();

                if (username.isEmpty() || email.isEmpty()) {
                    showFormError(form, "Username and email are required.");
                    return;
                }
                ValidationResult unCheck = ValidationUtils.requireUsername(username);
                if (!unCheck.valid) { showFormError(form, unCheck.message); return; }
                ValidationResult emCheck = ValidationUtils.requireEmail(email);
                if (!emCheck.valid) { showFormError(form, emCheck.message); return; }

                // TODO: PersistenceService.getInstance().updateUser(
                //     existingUsername, new User(null, username, null, role, oid, eid, email));
                showSuccess("User '" + username + "' updated (pending PersistenceService).");
                showTableView();
                loadData();
            },
            this::showTableView
        ));

        showForm(form);
    }

    // ── Form helpers ──────────────────────────────────────────────────────────

    /**
     * Shows the named form panel in the mainContent CardLayout.
     *
     * @param form the form panel to display
     */
    private void showForm(JPanel form) {
        mainContent.add(form, "form");
        ((CardLayout) mainContent.getLayout()).show(mainContent, "form");
    }

    /**
     * Returns to the table view in the mainContent CardLayout.
     */
    private void showTableView() {
        ((CardLayout) mainContent.getLayout()).show(mainContent, "tableView");
    }

    /**
     * Builds a titled form panel.
     *
     * @param title form heading text
     * @return configured form JPanel
     */
    private JPanel buildFormPanel(String title) {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(bgPrimary);
        form.setBorder(new EmptyBorder(24, 80, 24, 80));

        JLabel heading = new JLabel(title);
        heading.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        heading.setForeground(textPrimary);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        heading.setBorder(new EmptyBorder(0, 0, 20, 0));
        form.add(heading);

        return form;
    }

    /**
     * Builds a label + field row for a form.
     *
     * @param label field label text
     * @param field the input component
     * @return configured row JPanel
     */
    private JPanel fieldRow(String label, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(bgPrimary);
        row.setMaximumSize(new Dimension(600, 56));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        lbl.setForeground(textMuted);
        lbl.setPreferredSize(new Dimension(140, 36));

        field.setPreferredSize(new Dimension(340, 36));

        row.add(lbl,   BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    /**
     * Builds the Save and Back button row for a form.
     *
     * @param onSave action to run on Save
     * @param onBack action to run on Back
     * @return configured button row JPanel
     */
    private JPanel buildFormButtons(Runnable onSave, Runnable onBack) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setBackground(bgPrimary);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setBorder(new EmptyBorder(8, 0, 0, 0));

        JButton saveBtn = buildToolbarButton("Save",    e -> onSave.run());
        JButton backBtn = buildToolbarButton("← Back",  e -> onBack.run());

        row.add(saveBtn);
        row.add(backBtn);
        return row;
    }

    /**
     * Displays an inline error message on the form.
     *
     * @param form    the form panel
     * @param message error text to display
     */
    private void showFormError(JPanel form, String message) {
        for (Component c : form.getComponents()) {
            if (c instanceof JLabel lbl && "formError".equals(lbl.getName())) {
                lbl.setText(message);
                lbl.setVisible(true);
                return;
            }
        }
        JLabel err = new JLabel(message);
        err.setName("formError");
        err.setForeground(new Color(220, 80, 80));
        err.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        err.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(err);
        form.revalidate();
        form.repaint();
    }

    /**
     * Shows a success confirmation dialog.
     *
     * @param message success message text
     */
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Prompts the user to select a row before performing an action.
     *
     * @param entityType human-readable entity type (e.g. "organization", "user")
     */
    private void showNoSelectionPrompt(String entityType) {
        JOptionPane.showMessageDialog(this,
            "Please select an " + entityType + " from the table first.",
            "No Selection", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Shows a confirmation dialog before deleting a record.
     *
     * @param entityType human-readable entity type (e.g. "organization")
     * @param name       display name of the record to delete
     * @return true if the user confirmed deletion
     */
    private boolean confirmDelete(String entityType, String name) {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Delete " + entityType + " \"" + name + "\"?\nThis action cannot be undone.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }

    // ── Field factories ───────────────────────────────────────────────────────

    /** Builds a styled text field. */
    private JTextField styledField() {
        JTextField f = new JTextField();
        f.setBackground(bgSecondary);
        f.setForeground(textPrimary);
        f.setCaretColor(textPrimary);
        f.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            new EmptyBorder(4, 8, 4, 8)));
        return f;
    }

    /** Styles a password field to match the dark theme. */
    private void stylePasswordField(JPasswordField f) {
        f.setBackground(bgSecondary);
        f.setForeground(textPrimary);
        f.setCaretColor(textPrimary);
        f.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            new EmptyBorder(4, 8, 4, 8)));
    }

    /** Builds a styled combo box. */
    private JComboBox<String> styledCombo(String[] items) {
        JComboBox<String> box = new JComboBox<>(items);
        box.setBackground(bgSecondary);
        box.setForeground(textPrimary);
        box.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        return box;
    }

    // ── Export ────────────────────────────────────────────────────────────────

    /** Handles the Export toolbar action. */
    private void onExport() {
        JOptionPane.showMessageDialog(this, "Export — coming soon.");
    }

    // ── Stat card helpers ─────────────────────────────────────────────────────

    /**
     * Updates a stat card value by index (0–2).
     *
     * @param index stat card position (0-based)
     * @param value display value to set
     */
    private void setStatValue(int index, String value) {
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
        gbc.gridx = 0;

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
     * @param label  button display text
     * @param action ActionListener to wire
     * @return configured JButton
     */
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
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(bgTertiary);  }
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