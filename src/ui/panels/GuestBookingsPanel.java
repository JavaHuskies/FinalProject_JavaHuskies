package ui.panels;

import service.SessionManager;
import service.ThemeService;
import ui.ApplicationFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GuestBookingsPanel extends JPanel {

    private static final Color bgPrimary = ThemeService.colorBgPrimary;
    private static final Color bgSecondary = ThemeService.colorBgSecondary;
    private static final Color bgTertiary = ThemeService.colorBgTertiary;
    private static final Color textPrimary = ThemeService.colorTextPrimary;
    private static final Color textMuted = ThemeService.colorTextMuted;
    private static final Color borderColor = ThemeService.colorBorder;
    private final java.util.List<Object[]> bookingData = new java.util.ArrayList<>();
    private int bookingSequence = 4;

    private final ApplicationFrame frame;

    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JPanel statsRow;
    private JTable bookingsTable;

    public GuestBookingsPanel(ApplicationFrame frame) {
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

        titleLabel = new JLabel("Guest Bookings");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        titleLabel.setForeground(textPrimary);

        subtitleLabel = new JLabel("Manage your bookings");
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        subtitleLabel.setForeground(textMuted);

        header.add(titleLabel, BorderLayout.NORTH);
        header.add(subtitleLabel, BorderLayout.SOUTH);

        statsRow = new JPanel(new GridLayout(1, 3, 12, 0));
        statsRow.setBackground(bgPrimary);
        statsRow.add(buildStatCard("Total Bookings", "0"));
        statsRow.add(buildStatCard("Confirmed", "0"));
        statsRow.add(buildStatCard("Pending", "0"));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(bgPrimary);
        JButton newBtn = buildToolbarButton("New Booking");
        newBtn.addActionListener(e -> {
            String bookingId = "BK" + (System.currentTimeMillis() % 1000000000);
            bookingData.add(new Object[]{
                bookingId,
                "Dinner Reservation",
                "2026 04 20",
                2,
                "Confirmed"
            });
            loadData();
        });
        
        JButton editBtn = buildToolbarButton("Modify Booking");
        editBtn.addActionListener(e -> {
            int row = bookingsTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a booking to modify.");
                return;
            }

            Object[] booking = bookingData.get(row);

            String[] experiences = {
                "Theme Park Package",
                "Dinner Experience",
                "Resort Stay",
                "Dinner Reservation",
                "VIP Tour",
                "Casino Night"
            };

            JComboBox<String> experienceBox = new JComboBox<>(experiences);
            experienceBox.setSelectedItem(String.valueOf(booking[1]));

            int result = JOptionPane.showConfirmDialog(
                this,
                experienceBox,
                "Modify Experience",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                booking[1] = experienceBox.getSelectedItem();
                loadData();
            }
        });

        JButton cancelBtn = buildToolbarButton("Cancel Booking");
        cancelBtn.addActionListener(e -> {
            int row = bookingsTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a booking to cancel.");
                return;
            }

            bookingData.get(row)[4] = "Cancelled";
            loadData();
        });

        JButton refreshBtn = buildToolbarButton("Refresh");
        refreshBtn.addActionListener(e -> loadData());

        toolbar.add(newBtn);
        toolbar.add(editBtn);
        toolbar.add(cancelBtn);
        toolbar.add(refreshBtn);

        String[] columns = { "Booking ID", "Experience", "Date", "Party Size", "Status" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingsTable = styledTable(model);
        JScrollPane tableScroll = styledScroll(bookingsTable);

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBackground(bgPrimary);
        content.add(toolbar, BorderLayout.NORTH);
        content.add(tableScroll, BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setBackground(bgPrimary);
        top.add(header, BorderLayout.NORTH);
        top.add(statsRow, BorderLayout.SOUTH);

        wrapper.add(top, BorderLayout.NORTH);
        wrapper.add(content, BorderLayout.CENTER);

        add(wrapper, BorderLayout.CENTER);
    }

    private void loadData() {
        if (bookingData.isEmpty()) {
            bookingData.add(new Object[] { "BK1001", "Theme Park Package", "2026 04 05", 4, "Confirmed" });
            bookingData.add(new Object[] { "BK1002", "Dinner Experience", "2026 04 10", 2, "Pending" });
            bookingData.add(new Object[] { "BK1003", "Resort Stay", "2026 04 15", 3, "Cancelled" });
        }

        DefaultTableModel model = (DefaultTableModel) bookingsTable.getModel();
        model.setRowCount(0);

        int confirmed = 0;
        int pending = 0;

        for (Object[] row : bookingData) {
            model.addRow(row);

            String status = String.valueOf(row[4]);
            if ("Confirmed".equalsIgnoreCase(status)) {
                confirmed++;
            }
            if ("Pending".equalsIgnoreCase(status)) {
                pending++;
            }
        }

        setStatValue(0, String.valueOf(bookingData.size()));
        setStatValue(1, String.valueOf(confirmed));
        setStatValue(2, String.valueOf(pending));
    }

    private void updateHeader() {
        subtitleLabel.setText("Guest ID: " + SessionManager.getUserId());
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