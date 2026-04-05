/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui.panels;

/**
 *
 * @author fabio
 */

import service.ThemeService;
import ui.ApplicationFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import service.NotificationService;

public class GuestRegistrationPanel extends JPanel {

    private final ApplicationFrame frame;

    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JCheckBox termsCheck;

    public GuestRegistrationPanel(ApplicationFrame frame) {
        this.frame = frame;
        setBackground(ThemeService.colorBgPrimary);
        setLayout(new BorderLayout());
        buildComponents();
    }

    private void buildComponents() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(ThemeService.colorBgPrimary);
        wrapper.setBorder(new EmptyBorder(30, 60, 30, 60));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(ThemeService.colorBgSecondary);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeService.colorBorder, 1),
                new EmptyBorder(24, 24, 24, 24)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        JLabel title = new JLabel("Guest Registration", SwingConstants.CENTER);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        title.setForeground(ThemeService.colorTextPrimary);
        card.add(title, gbc);

        gbc.gridy++;
        JLabel subtitle = new JLabel("Create a guest account", SwingConstants.CENTER);
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        subtitle.setForeground(ThemeService.colorTextMuted);
        card.add(subtitle, gbc);

        gbc.gridwidth = 1;

        gbc.gridy++;
        gbc.gridx = 0;
        card.add(buildLabel("First Name"), gbc);
        gbc.gridx = 1;
        firstNameField = buildTextField();
        card.add(firstNameField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        card.add(buildLabel("Last Name"), gbc);
        gbc.gridx = 1;
        lastNameField = buildTextField();
        card.add(lastNameField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        card.add(buildLabel("Email"), gbc);
        gbc.gridx = 1;
        emailField = buildTextField();
        card.add(emailField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        card.add(buildLabel("Phone"), gbc);
        gbc.gridx = 1;
        phoneField = buildTextField();
        card.add(phoneField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        card.add(buildLabel("Address"), gbc);
        gbc.gridx = 1;
        addressField = buildTextField();
        card.add(addressField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        card.add(buildLabel("Password"), gbc);
        gbc.gridx = 1;
        passwordField = buildPasswordField();
        card.add(passwordField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        card.add(buildLabel("Confirm Password"), gbc);
        gbc.gridx = 1;
        confirmPasswordField = buildPasswordField();
        card.add(confirmPasswordField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        termsCheck = new JCheckBox("I agree to the terms");
        termsCheck.setOpaque(false);
        termsCheck.setForeground(ThemeService.colorTextPrimary);
        card.add(termsCheck, gbc);

        gbc.gridy++;
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttons.setOpaque(false);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> handleRegister());

        JButton backButton = new JButton("Back to Login");
        backButton.addActionListener(e -> frame.showPanel(ApplicationFrame.panelGuestLogin));

        buttons.add(registerButton);
        buttons.add(backButton);
        card.add(buttons, gbc);

        wrapper.add(card);
        add(wrapper, BorderLayout.CENTER);
    }

    private JLabel buildLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(ThemeService.colorTextPrimary);
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        return label;
    }

    private JTextField buildTextField() {
        JTextField field = new JTextField(20);
        field.setBackground(ThemeService.colorBgTertiary);
        field.setForeground(ThemeService.colorTextPrimary);
        field.setCaretColor(ThemeService.colorTextPrimary);
        return field;
    }

    private JPasswordField buildPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setBackground(ThemeService.colorBgTertiary);
        field.setForeground(ThemeService.colorTextPrimary);
        field.setCaretColor(ThemeService.colorTextPrimary);
        return field;
    }

    private void handleRegister() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                || phone.isEmpty() || address.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.");
            return;
        }

        if (!termsCheck.isSelected()) {
            JOptionPane.showMessageDialog(this, "You must agree to the terms.");
            return;
        }
        
        String verificationToken = "VT" + System.currentTimeMillis();
        NotificationService.sendVerificationEmail(email, verificationToken);

        JOptionPane.showMessageDialog(
        this,
        "Registration successful.\n\n" +
        "Verification email sent to: " + email + "\n" +
        "Verification token: " + verificationToken
    );
        clearForm();
        frame.showPanel(ApplicationFrame.panelGuestLogin);
    }
    
        private void clearForm() {
            firstNameField.setText("");
            lastNameField.setText("");
            emailField.setText("");
            phoneField.setText("");
            addressField.setText("");
            passwordField.setText("");
            confirmPasswordField.setText("");
            termsCheck.setSelected(false);
    }
        
        public void onShow() {
            clearForm();
}
}
