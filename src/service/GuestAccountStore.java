/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author fabio
 */

import java.util.HashMap;
import java.util.Map;

public class GuestAccountStore {

    public static class GuestAccount {
        private final String firstName;
        private final String lastName;
        private final String email;
        private final String phone;
        private final String address;
        private final String password;
        private String verificationToken;
        private boolean emailVerified;

        public GuestAccount(String firstName, String lastName, String email,
                            String phone, String address, String password,
                            String verificationToken) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.phone = phone;
            this.address = address;
            this.password = password;
            this.verificationToken = verificationToken;
            this.emailVerified = false;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public String getAddress() {
            return address;
        }

        public String getPassword() {
            return password;
        }

        public String getVerificationToken() {
            return verificationToken;
        }

        public void setVerificationToken(String verificationToken) {
            this.verificationToken = verificationToken;
        }

        public boolean isEmailVerified() {
            return emailVerified;
        }

        public void setEmailVerified(boolean emailVerified) {
            this.emailVerified = emailVerified;
        }
    }

    private static final Map<String, GuestAccount> accountsByEmail = new HashMap<>();

    public static boolean exists(String email) {
        return accountsByEmail.containsKey(email.toLowerCase());
    }

    public static void save(GuestAccount account) {
        accountsByEmail.put(account.getEmail().toLowerCase(), account);
    }

    public static GuestAccount findByEmail(String email) {
        if (email == null) {
            return null;
        }
        return accountsByEmail.get(email.toLowerCase());
    }

    public static boolean verify(String email, String token) {
        GuestAccount account = findByEmail(email);
        if (account == null) {
            return false;
        }
        if (account.getVerificationToken() == null) {
            return false;
        }
        if (!account.getVerificationToken().equals(token)) {
            return false;
        }
        account.setEmailVerified(true);
        return true;
    }
}
