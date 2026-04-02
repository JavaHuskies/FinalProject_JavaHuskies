package ui.panels;

import service.SessionManager;
import model.Claims;
import ui.ApplicationFrame;

import javax.swing.table.DefaultTableModel;

public class CreativeLeadPanel extends WorkAreaTemplate {

    public CreativeLeadPanel(ApplicationFrame frame) {
        super(frame);
        setPageTitle("Creative Lead Dashboard");
        setPageSubtitle("Creative Operations");
    }

    @Override
    public void onShow() {
        if (!SessionManager.guard(Claims.roleCreativeLead)) {
            getFrame().showPanel(ApplicationFrame.panelStaffLogin);
            return;
        }
        super.onShow();
    }

    @Override
    protected void loadData() {
        // KPIs (existing)
        setStatValue(0, "5");   // Active Creative Tasks
        setStatValue(1, "2");   // Pending Reviews
        setStatValue(2, "8");   // Completed Assets

        // Table (existing Work Request structure)
        DefaultTableModel m = (DefaultTableModel) getDataTable().getModel();
        m.setRowCount(0);
        m.addRow(new Object[]{ "WR-301", "Poster Design", "Marketing", "Creative", "In Progress" });
        m.addRow(new Object[]{ "WR-302", "Video Edit", "Broadcast", "Creative", "Pending Review" });
        m.addRow(new Object[]{ "WR-303", "Brand Refresh", "Executive", "Creative", "Open" });
    }
}
