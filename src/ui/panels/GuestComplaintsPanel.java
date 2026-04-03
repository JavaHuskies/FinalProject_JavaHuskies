/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui.panels;

/**
 *
 * @author fabio
 */

import service.SessionManager;
import service.ThemeService;
import ui.ApplicationFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GuestComplaintsPanel extends JPanel {

    private static final Color bgPrimary = ThemeService.colorBgPrimary;
    private static final Color bgSecondary = ThemeService.colorBgSecondary;
    private static final Color bgTertiary = ThemeService.colorBgTertiary;
    private static final Color textPrimary = ThemeService.colorTextPrimary;
    private static final Color textMuted = ThemeService.colorTextMuted;
    private static final Color borderColor = ThemeService.colorBorder;

    private final ApplicationFrame frame;

    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JPanel statsRow;
    private JTable complaintsTable;
    private JTextField subjectField;
    private JTextArea descriptionArea;
    private JTextField targetOrgField;

    public GuestComplaintsPanel(ApplicationFrame frame) {
        this.frame = frame;
        setBackground(bgPrimary);
        setLayout(new BorderLayout());
        buildComponents();
    }

    public void onShow() {
        if (!SessionManager.isGuest()) {
            frame.showPanel(ApplicationFrame.panelGuestLogin);
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

        titleLabel = new JLabel("Guest Complaints");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        titleLabel.setForeground(textPrimary);

        subtitleLabel = new JLabel("Submit and track complaints");
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        subtitleLabel.setForeground(textMuted);

        header.add(titleLabel, BorderLayout.NORTH);
        header.add(subtitleLabel, BorderLayout.SOUTH);

        statsRow = new JPanel(new GridLayout(1, 3, 12, 0));
        statsRow.setBackground(bgPrimary);
        statsRow.add(buildStatCard("Total Complaints", "0"));
        statsRow.add(buildStatCard("Open", "0"));
        statsRow.add(buildStatCard("Resolved", "0"));

        JPanel formPanel = buildFormPanel();

        String[] columns = { "Complaint ID", "Subject", "Target Org", "Submitted", "Status" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        complaintsTable = styledTable(model);
        JScrollPane tableScroll = styledScroll(complaintsTable);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBackground(bgPrimary);
        content.add(formPanel, BorderLayout.NORTH);
        content.add(tableScroll, BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setBackground(bgPrimary);
        top.add(header, BorderLayout.NORTH);
        top.add(statsRow, BorderLayout.SOUTH);

        wrapper.add(top, BorderLayout.NORTH);
        wrapper.add(content, BorderLayout.CENTER);

        add(wrapper, BorderLayout.CENTER);
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(bgSecondary);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBackground(bgSecondary);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        fields.add(buildLabel("Subject"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        subjectField = styledTextField();
        fields.add(subjectField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        fields.add(buildLabel("Target Org"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        targetOrgField = styledTextField();
        fields.add(targetOrgField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        fields.add(buildLabel("Description"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        descriptionArea.setBackground(bgTertiary);
        descriptionArea.setForeground(textPrimary);
        descriptionArea.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane areaScroll = new JScrollPane(descriptionArea);
        areaScroll.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        areaScroll.getViewport().setBackground(bgTertiary);
        fields.add(areaScroll, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttons.setBackground(bgSecondary);

        JButton submitBtn = buildToolbarButton("Submit Complaint");
        submitBtn.addActionListener(e -> submitComplaintPlaceholder());

        JButton clearBtn = buildToolbarButton("Clear");
        clearBtn.addActionListener(e -> clearForm());

        buttons.add(submitBtn);
        buttons.add(clearBtn);

        panel.add(fields, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);

        return panel;
    }

    private void loadData() {
        DefaultTableModel model = (DefaultTableModel) complaintsTable.getModel();
        model.setRowCount(0);

        model.addRow(new Object[] { "C001", "Service issue", "Theme Worlds", "2026 04 01", "Open" });
        model.addRow(new Object[] { "C002", "Billing question", "Milliways", "2026 03 28", "Resolved" });
        model.addRow(new Object[] { "C003", "Booking problem", "Theme Worlds", "2026 03 25", "In Review" });

        setStatValue(0, "3");
        setStatValue(1, "1");
        setStatValue(2, "1");
    }

    private void updateHeader() {
        subtitleLabel.setText("Guest ID: " + SessionManager.getUserId());
    }

    private void submitComplaintPlaceholder() {
        String subject = subjectField.getText().trim();
        String targetOrg = targetOrgField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (subject.isBlank() || targetOrg.isBlank() || description.isBlank()) {
            JOptionPane.showMessageDialog(this, "Please fill in all complaint fields.");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) complaintsTable.getModel();
        String complaintId = "C" + String.format("%03d", model.getRowCount() + 1);

        model.addRow(new Object[] {
                complaintId,
                subject,
                targetOrg,
                "2026 04 02",
                "Open"
        });

        setStatValue(0, String.valueOf(model.getRowCount()));
        JOptionPane.showMessageDialog(this, "Complaint submitted. Placeholder action only.");
        clearForm();
    }

    private void clearForm() {
        subjectField.setText("");
        targetOrgField.setText("");
        descriptionArea.setText("");
    }

    private JLabel buildLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        label.setForeground(textMuted);
        return label;
    }

    private JTextField styledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        field.setBackground(bgTertiary);
        field.setForeground(textPrimary);
        field.setCaretColor(textPrimary);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                new EmptyBorder(8, 8, 8, 8)
        ));
        return field;
    }

    private JPanel buildStatCard(String label, String value) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(bgSecondary);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

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

    private void setStatValue(int index, String value) {
        JPanel card = (JPanel) statsRow.getComponent(index);
        for (Component c : card.getComponents()) {
            if (c instanceof JLabel lbl && lbl.getFont().getSize() >= 22) {
                lbl.setText(value);
                break;
            }
        }
    }

    private JButton buildToolbarButton(String label) {
        JButton btn = new JButton(label);
        btn.setBackground(bgTertiary);
        btn.setForeground(textPrimary);
        btn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                new EmptyBorder(6, 14, 6, 14)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setBackground(bgSecondary);
        table.setForeground(textPrimary);
        table.setGridColor(borderColor);
        table.setRowHeight(34);
        table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        table.setSelectionBackground(bgTertiary);
        table.setSelectionForeground(textPrimary);
        table.setShowVerticalLines(false);
        table.getTableHeader().setBackground(bgTertiary);
        table.getTableHeader().setForeground(textMuted);
        table.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        return table;
    }

    private JScrollPane styledScroll(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(bgSecondary);
        scroll.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        return scroll;
    }
}