package ua.ipt.users;

import java.util.Scanner;

import static ua.ipt.MyApp.checkIfUserExists;


/**
 * Created by Roman Horilyi on 18.03.2016.
 */
public class Admin extends User {

    public static final String USERNAME = "ADMIN";

    public Admin(String password) {
        super(USERNAME, password);
    }

    public void changePasswordFirstAdminEntry() {
        String newPassword = checkNewPassword();
        updatePasswordInDB(newPassword);
        setPassword(newPassword);
    }

    public void addNewUser() {
        System.out.print("\nEnter username > ");
        Scanner sc = new Scanner(System.in);
        StringBuilder username = new StringBuilder();
        username.replace(0, username.length(), sc.nextLine());

        while (checkIfUserExists(username.toString())) {
            System.out.println("\nError. There is a user with such username.");
            System.out.print("Please try again > ");
            username.replace(0, username.length(), sc.nextLine());
        }

        String password = checkNewUserPassword();

        User newUser = new User(username.toString(), password);
        newUser.addUserToDB();
        System.out.println("Successfully added " + username + " to DB!");
    }

    // is used only for addition of new user, so we have no need to check if password differ from the old one
    public String checkNewUserPassword() {
        System.out.print("Enter password > ");
        Scanner sc = new Scanner(System.in);
        StringBuilder password = new StringBuilder();
        password.replace(0, password.length(), sc.nextLine());

        checkIfPasswordMatchesPattern(password);
        confirmNewPassword(password);
        return password.toString();
    }
}