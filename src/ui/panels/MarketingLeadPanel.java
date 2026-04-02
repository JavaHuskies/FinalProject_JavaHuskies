package ui.panels;

import service.SessionManager;
import model.Claims;
import ui.ApplicationFrame;

import javax.swing.table.DefaultTableModel;

public class MarketingLeadPanel extends WorkAreaTemplate {

    public MarketingLeadPanel(ApplicationFrame frame) {
        super(frame);
        setPageTitle("Marketing Lead Dashboard");
        setPageSubtitle("Marketing Operations");
    }

    @Override
    public void onShow() {
        if (!SessionManager.guard(Claims.roleMarketingLead)) {
            getFrame().showPanel(ApplicationFrame.panelStaffLogin);
            return;
        }
        super.onShow();
    }

    @Override
    protected void loadData() {
        // KPIs (existing)
        setStatValue(0, "4");   // Active Campaigns
        setStatValue(1, "1");   // Pending Approvals
        setStatValue(2, "5");   // Work Requests

        // Table (existing Work Request structure)
        DefaultTableModel m = (DefaultTableModel) getDataTable().getModel();
        m.setRowCount(0);
        m.addRow(new Object[]{ "WR-501", "Q3 Retail Campaign", "Retail", "Marketing", "Active" });
        m.addRow(new Object[]{ "WR-502", "Broadcast Promo", "Broadcast", "Marketing", "Pending Approval" });
        m.addRow(new Object[]{ "WR-503", "Digital Ads Refresh", "Digital", "Marketing", "Open" });
    }
}
