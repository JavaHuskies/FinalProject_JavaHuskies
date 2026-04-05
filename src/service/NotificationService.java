/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author fabio
 */


import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class NotificationService {

    private static final String HOST = "smtp.gmail.com";
    private static final String PORT = "587";

    private static String email;
    private static String password;

    static {
        // Load from config.properties later
        ConfigService cfg = ConfigService.getInstance();
        email = cfg.get("smtp.user", "");
        password = cfg.get("smtp.password", "");
    }

    public static void sendEmail(String to, String subject, String body) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", HOST);
            props.put("mail.smtp.port", PORT);

            Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(email, password);
                    }
                });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));
            message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(to)
            );
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

            System.out.println("Email sent to " + to);

        } catch (Exception e) {
            System.out.println("Email failed: " + e.getMessage());
        }
    }

    public static void sendVerificationEmail(String to, String token) {
        String subject = "Verify your account";
        String body = "Your verification code is: " + token;

        sendEmail(to, subject, body);
    }
}