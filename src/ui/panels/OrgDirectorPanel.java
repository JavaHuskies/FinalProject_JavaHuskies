package ui.panels;

import service.SessionManager;
import model.Claims;
import ui.ApplicationFrame;

import javax.swing.table.DefaultTableModel;

public class OrgDirectorPanel extends WorkAreaTemplate {

    public OrgDirectorPanel(ApplicationFrame frame) {
        super(frame);
        setPageTitle("Organization Director");
        setPageSubtitle("Organization Overview");
    }

    @Override
    public void onShow() {
        if (!SessionManager.guard(Claims.roleOrgDirector)) {
            getFrame().showPanel(ApplicationFrame.panelStaffLogin);
            return;
        }
        super.onShow();
    }

    @Override
    protected void loadData() {
        // KPIs (existing)
        setStatValue(0, "12");   // Open Work Requests
        setStatValue(1, "4");    // Active Issues
        setStatValue(2, "3");    // Pending Approvals

        // Table (existing Work Request structure)
        DefaultTableModel m = (DefaultTableModel) getDataTable().getModel();
        m.setRowCount(0);
        m.addRow(new Object[]{ "WR-201", "Budget Review", "Finance", "Compliance", "Pending" });
        m.addRow(new Object[]{ "WR-202", "Campaign Asset Request", "Marketing", "Creative", "Open" });
        m.addRow(new Object[]{ "WR-203", "System Access Request", "IT", "Technology", "In Review" });
    }
}
