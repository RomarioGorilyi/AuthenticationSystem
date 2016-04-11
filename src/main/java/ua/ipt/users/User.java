package ua.ipt.users;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import static ua.ipt.database.Connector.getConnection;


/**
 * Created by Roman Horilyi on 18.03.2016.
 */
public class User {

    private String username;
    private String password;


    public void setPassword(String password) {
        this.password = password;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void addUserToDB() {
        try {
            String query = "insert into users(Username, Password) values(?, ?)";
            PreparedStatement insert = getConnection().prepareStatement(query);
            insert.setString(1, username);
            insert.setString(2, password);
            insert.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Change password of the user, if difference between time of the last password change and current time
     * is bigger than 24 hours.
     */
    public void changePassword() {
        if (checkTimeOfLastPasswordChange()) {
            validateOldPassword();
            String newPassword = validateNewPassword();
            setPassword(newPassword);
            updatePasswordInDB(newPassword);
        } else {
            System.out.println("It's too early to change password. Try a bit later.");
        }
    }

    /**
     * Check if user is able to change his password.
     * @return TRUE if difference between time of the last password change and current time is bigger than 24 hours
     */
    public boolean checkTimeOfLastPasswordChange() {
        boolean ifItIsAppropriateTime = false;

        try {
            String query = "SELECT * FROM users WHERE Username = ? " +
                    "AND HOUR(TIMEDIFF(NOW(), LastPasswordChange)) > 24";
            PreparedStatement select = getConnection().prepareStatement(query);
            select.setString(1, username);

            ResultSet result = select.executeQuery();
            if (result.next()) {
                ifItIsAppropriateTime = true;
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        return ifItIsAppropriateTime;
    }

    public void validateOldPassword() {
        System.out.print("\nEnter old password > ");
        Scanner sc = new Scanner(System.in);
        StringBuilder oldPassword = new StringBuilder();
        oldPassword.replace(0, oldPassword.length(), sc.nextLine());

        if (!password.equals(oldPassword.toString())) {
            System.out.println("\nError. Incorrect old password.\nPlease, try again.");
            validateOldPassword();
        }
    }

    /**
     * Check if new password corresponds to all set requirements.
     * @return new password
     */
    public String validateNewPassword() {
        System.out.print("Enter new password > ");
        Scanner sc = new Scanner(System.in);
        StringBuilder newPassword = new StringBuilder();
        newPassword.replace(0, newPassword.length(), sc.nextLine());

        checkIfNewPasswordDifferFromOld(newPassword);
        checkIfPasswordMatchesPattern(newPassword);
        confirmNewPassword(newPassword);

        return newPassword.toString();
    }

    public void checkIfNewPasswordDifferFromOld(StringBuilder newPassword) {
        if (newPassword.toString().equals(password)) {
            System.out.println("\nError. You should enter new password different from the old one." +
                    "\nPlease, try again.");
            validateNewPassword();
        }
    }

    public void checkIfPasswordMatchesPattern(StringBuilder newPassword) {
        if (!newPassword.toString().matches("((?=.*[a-zA-Z])(?=.*[а-яА-Я]).+)")) {
            // (?=.*[a-zA-Z]) - must contains at least one Latin character
            // (?=.*[а-яА-Я]) - must contains at least one Cyrillic character
            System.out.println("\nError. You must use both Latin and Cyrillic characters in password." +
                    "\nPlease, try again.");
            validateNewPassword();
        }

        if (newPassword.length() < 8) {
            System.out.println("\nError. Password is too short. Min length must be more than 8 symbols." +
                    "\nPlease, try again.");
            validateNewPassword();
        }

        if (newPassword.length() > 32) {
            System.out.println("\nError. Password is too long. Max length must be less than 32 symbols." +
                    "\nPlease, try again.");
            validateNewPassword();
        }
    }

    public void confirmNewPassword(StringBuilder newPassword) {
        System.out.print("Confirm new password > ");
        Scanner sc1 = new Scanner(System.in);
        StringBuilder passwordConfirmation = new StringBuilder();
        passwordConfirmation.replace(0, passwordConfirmation.length(), sc1.nextLine());

        if (!newPassword.toString().equals(passwordConfirmation.toString())) {
            System.out.println("\nError. Your passwords don't match up.\nPlease, try again.");
            validateNewPassword();
        }
    }

    public void updatePasswordInDB(String newPassword) {
        try {
            String query = "UPDATE users SET Password = ? WHERE Username = ?";
            PreparedStatement update = getConnection().prepareStatement(query);
            update.setString(1, newPassword);
            update.setString(2, username);
            update.executeUpdate();
            System.out.println("Password was changed successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}