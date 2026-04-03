package ui.panels;

import service.SessionManager;
import model.Claims;
import ui.ApplicationFrame;

import javax.swing.table.DefaultTableModel;

public class TechnologyLeadPanel extends WorkAreaTemplate {

    public TechnologyLeadPanel(ApplicationFrame frame) {
        super(frame);
        setPageTitle("Technology Lead Dashboard");
        setPageSubtitle("Technical Operations");
    }

    @Override
    public void onShow() {
        if (!SessionManager.guard(Claims.roleTechnologyLead)) {
            getFrame().showPanel(ApplicationFrame.panelStaffLogin);
            return;
        }
        super.onShow();
    }

    @Override
    protected void loadData() {
        // KPIs (existing)
        setStatValue(0, "7");   // Open Issues
        setStatValue(1, "3");   // Deployments Pending
        setStatValue(2, "6");   // Active Work Requests

        // Table (existing Work Request structure)
        DefaultTableModel m = (DefaultTableModel) getDataTable().getModel();
        m.setRowCount(0);
        m.addRow(new Object[]{ "WR-401", "API Outage Investigation", "IT", "Technology", "Open" });
        m.addRow(new Object[]{ "WR-402", "System Upgrade", "Network", "Technology", "Scheduled" });
        m.addRow(new Object[]{ "WR-403", "Access Provisioning", "HR", "Technology", "In Progress" });
    }
}
