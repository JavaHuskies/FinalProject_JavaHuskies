package ui.panels;

import model.Claims;
import service.SessionManager;
import ui.ApplicationFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;

public class CfoPanel extends WorkAreaTemplate {

    public CfoPanel(ApplicationFrame frame) {
        super(frame);

        setPageTitle("CFO Dashboard");
        setPageSubtitle("Enterprise Financial Overview");

        initFinanceTable();
        initToolbar();
    }

    private void initFinanceTable() {
        String[] cols = { "Organization", "Budget", "Spend", "Variance", "Status" };

        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        getDataTable().setModel(model);
    }

    private void initToolbar() {
        JPanel toolbar = getToolbar();
        toolbar.removeAll();

        // Refresh button
        JButton refresh = buildToolbarButton("Refresh");
        refresh.addActionListener(e -> loadData());

        // Export button (simple CSV export)
        JButton export = buildToolbarButton("Export");
        export.addActionListener(e -> exportCsv());

        toolbar.add(refresh);
        toolbar.add(export);
    }

    @Override
    public void onShow() {
        if (!SessionManager.guard(Claims.roleGroupCfo)) {
            getFrame().showPanel(ApplicationFrame.panelStaffLogin);
            return;
        }
        loadData();
    }

    private void loadData() {
        DefaultTableModel model = (DefaultTableModel) getDataTable().getModel();
        model.setRowCount(0);

        // Placeholder data — replace with PersistenceService later
        model.addRow(new Object[]{ "Slartibartfast Pictures", "$1.2M", "$980k", "$220k", "On Track" });
        model.addRow(new Object[]{ "Magrathea Studios", "$3.4M", "$3.8M", "-$400k", "Over Budget" });
        model.addRow(new Object[]{ "Pan Galactic Broadcast", "$2.1M", "$1.9M", "$200k", "On Track" });

        // Stat cards
        setStatValue(0, "$6.7M");   // Total Budget
        setStatValue(1, "$6.68M");  // Total Spend
        setStatValue(2, "$20k");    // Net Variance
    }

    // -------------------------------------------------------------------------
    // Simple CSV export — no dependencies, consistent with ReportingPanel style
    // -------------------------------------------------------------------------
    private void exportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export CFO Report");

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            try (PrintWriter out = new PrintWriter(file)) {
                DefaultTableModel model = (DefaultTableModel) getDataTable().getModel();

                // Write headers
                for (int c = 0; c < model.getColumnCount(); c++) {
                    out.print(model.getColumnName(c));
                    if (c < model.getColumnCount() - 1) out.print(",");
                }
                out.println();

                // Write rows
                for (int r = 0; r < model.getRowCount(); r++) {
                    for (int c = 0; c < model.getColumnCount(); c++) {
                        out.print(model.getValueAt(r, c));
                        if (c < model.getColumnCount() - 1) out.print(",");
                    }
                    out.println();
                }

                JOptionPane.showMessageDialog(this,
                        "Export complete:\n" + file.getAbsolutePath(),
                        "Export Successful",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Export failed:\n" + ex.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
