package ui.panels;

import ui.ApplicationFrame;
import service.ThemeService;

import javax.swing.*;
import java.awt.*;

public class AiGuidePanel extends JPanel {

    private final ApplicationFrame frame;

    public AiGuidePanel(ApplicationFrame frame) {
        this.frame = frame;

        setBackground(ThemeService.colorBgPrimary);
        setLayout(new BorderLayout());

        JLabel lbl = new JLabel("H2G2 Guide — Coming Soon", SwingConstants.CENTER);
        lbl.setForeground(ThemeService.colorTextPrimary);
        lbl.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));

        add(lbl, BorderLayout.CENTER);
    }

    public void onShow() {
        // No data to load yet — placeholder
    }
}
