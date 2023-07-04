package com.project.dchat.Entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

public class User implements Serializable {
    private String username;
    private String password;
    private String mail;
    private boolean isActive;
    private String activationCode;
    private LocalDateTime confirmationTimeout;

    public User(String username, String password, String mail) {
        this.confirmationTimeout = LocalDateTime.now().plusMinutes(5);
        this.isActive = false;
        this.mail = mail == null ? null : mail.trim();
        this.username = username.trim();
        this.password = password.trim();
        this.activationCode = generateRandomNum().trim();
    }

    public String generateActivationCode() {
        this.activationCode = generateRandomNum();
        this.confirmationTimeout = LocalDateTime.now().plusMinutes(5);
        return activationCode;
    }

    private static String generateRandomNum() {
        Random random = new Random();
        int length = random.nextInt(9) + 8;
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = String.valueOf(random.nextInt(10)).charAt(0);
        }
        return new String(chars);
    }

    private static String generateRandomString() {
        Random random = new Random();
        int length = random.nextInt(9) + 8;
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = (char) (random.nextInt(65) + 32);
        }
        return new String(chars);
    }

    public void generateNewPassword(){
        this.password = generateRandomString();
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", mail='" + mail + '\'' +
                ", isActive=" + isActive +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) && Objects.equals(password, user.password) && Objects.equals(mail, user.mail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, mail);
    }

    public String getActivationCode() {
        return activationCode;
    }

    public String getMail() {
        return mail;
    }

    public String getUsername() {
        return username;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getPassword() {
        return password;
    }

    public boolean isExcist() {
        return username != null && password != null && mail != null;
    }

    public void activate() {
        isActive = true;
    }

    public boolean isActivationValid() {
        return LocalDateTime.now().isBefore(confirmationTimeout);
    }

    public boolean subEquals(User sender) {
        if (sender.getUsername().equalsIgnoreCase(this.username)) return true;
        if (sender.getMail().equalsIgnoreCase(this.mail)) return true;
        return false;
    }

    public boolean loginAccess(User sender) {
        return this.username.equals(sender.getUsername()) && this.password.equals(sender.getPassword());
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
