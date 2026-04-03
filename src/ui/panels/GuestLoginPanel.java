
package ui.panels;

import ui.ApplicationFrame;
import service.SessionManager;
import service.ThemeService;

import javax.swing.*;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author fabio
 */

import service.SessionManager;
import service.ThemeService;
import ui.ApplicationFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GuestLoginPanel extends JPanel {

    private final ApplicationFrame frame;
    private JTextField usernameField;

    public GuestLoginPanel(ApplicationFrame frame) {
        this.frame = frame;
        setBackground(ThemeService.colorBgPrimary);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Guest Login", SwingConstants.CENTER);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        title.setForeground(ThemeService.colorTextPrimary);

        gbc.gridy = 0;
        add(title, gbc);

        usernameField = new JTextField(20);
        gbc.gridy = 1;
        add(usernameField, gbc);

        JButton btn = new JButton("Continue");
        btn.addActionListener(e -> attemptLogin());
        gbc.gridy = 2;
        add(btn, gbc);

    }

    private void attemptLogin() {
        String user = usernameField.getText().trim();
        if (user.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a username.");
            return;
        }

        SessionManager.injectGuestSession(user);
        frame.routeByRole();
    }

    public void onShow() {
        usernameField.setText("");
        usernameField.requestFocusInWindow();
    }

}

