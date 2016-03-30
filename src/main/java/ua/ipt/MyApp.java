package ua.ipt;

import ua.ipt.users.Admin;
import ua.ipt.users.User;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import static ua.ipt.database.Connector.connect;
import static ua.ipt.database.Connector.disconnect;
import static ua.ipt.database.Connector.getConnection;


/**
 * Created by Roman Horilyi on 18.03.2016.
 */
public class MyApp {
    public static void main(String[] args) throws IOException {
        System.out.println("----------------------------MyApp----------------------------");
        showMenu();
    }

    /**
     * Starting menu for unregistered in the system users
     */
    public static void showMenu() throws IOException {
        System.out.println("\n*** Main menu ***");
        System.out.println("* Enter the system - 1");
        System.out.println("* Help - 2");
        System.out.println("* Exit - 3");

        System.out.print("Enter the number of command > ");
        Scanner sc = new Scanner(System.in);
        String command = sc.next();
        while (!command.matches("^[1-3]$")) {
            System.out.println("\nError. You inserted invalid number.");
            System.out.print("Please, try again > ");
            command = sc.next();
        }
        int commandNumber = Integer.parseInt(command);

        switch (commandNumber) {
            case 1:
                enterAsUser();
                break;
            case 2:
                showHelp();

                System.out.print("\nPress <Enter> for return to the menu > ");
                Scanner readInput = new Scanner(System.in);
                command = readInput.nextLine();
                if (command.equals("")) {
                    showMenu();
                }
                break;
            case 3:
                exit();
                break;
            default: break;
        }
    }

    /**
     * ADMIN menu
     */
    public static void showMenu(Admin admin) throws IOException {
        System.out.println("\n*** Admin menu ***");
        System.out.println("* Change password - 1");
        System.out.println("* Show all users - 2");
        System.out.println("* Add new user - 3");
        System.out.println("* Block user - 4");
        System.out.println("* Help - 5");
        System.out.println("* Exit - 6");

        System.out.print("Enter the number of command > ");
        Scanner sc = new Scanner(System.in);
        String command = sc.next();
        while (!command.matches("^[1-6]$")) {
            System.out.println("\nError. You inserted invalid number.");
            System.out.print("Please, try again > ");
            command = sc.next();
        }
        int commandNumber = Integer.parseInt(command);

        switch (commandNumber) {
            case 1:
                admin.changePassword();
                showMenu(admin);
                break;
            case 2:
                showAllUsers();

                System.out.print("\nPress <Enter> for return to the menu > ");
                Scanner readInput = new Scanner(System.in);
                command = readInput.nextLine();
                if (command.equals("")) {
                    showMenu(admin);
                }
                break;
            case 3:
                admin.addNewUser();
                showMenu(admin);
                break;
            case 4:
                System.out.print("Enter username of user you want to block > ");
                readInput = new Scanner(System.in);
                StringBuilder username = new StringBuilder();
                username.append(readInput.nextLine());

                while (!checkIfUserExists(username.toString())) {
                    System.out.println("\nError. There is no user with such username.");
                    System.out.print("Please try again > ");
                    username.replace(0, username.length(), sc.nextLine());
                }

                blockUser(username.toString());

                System.out.print("\nPress <Enter> for return to the menu > ");
                readInput = new Scanner(System.in);
                command = readInput.nextLine();
                if (command.equals("")) {
                    showMenu(admin);
                }
                break;
            case 5:
                showHelp();

                System.out.print("\nPress <Enter> for return to the menu > ");
                readInput = new Scanner(System.in);
                command = readInput.nextLine();
                if (command.equals("")) {
                    showMenu(admin);
                }
                break;
            case 6:
                exit();
            default: break;
        }
    }

    /**
     * User menu
     */
    public static void showMenu(User user) throws IOException {
        System.out.println("\n*** User menu ***");
        System.out.println("* Change password - 1");
        System.out.println("* Help - 2");
        System.out.println("* Exit - 3");

        System.out.print("Enter the number of command > ");
        Scanner sc = new Scanner(System.in);
        String command = sc.next();
        while (!command.matches("^[1-3]$")) {
            System.out.println("\nError. You inserted invalid number.");
            System.out.print("Please, try again > ");
            command = sc.next();
        }
        int commandNumber = Integer.parseInt(command);

        switch (commandNumber) {
            case 1:
                user.changePassword();
                showMenu(user);
                break;
            case 2:
                showHelp();

                System.out.print("\nPress <Enter> for return to the menu > ");
                Scanner readInput = new Scanner(System.in);
                command = readInput.nextLine();
                if (command.equals("")) {
                    showMenu(user);
                }
                break;
            case 3:
                exit();
                break;
            default: break;
        }
    }

    public static void showAllUsers() {
        try {
            String query = "SELECT * FROM users WHERE IDUser <> 1"; // show all users except for ADMIN
            Statement select = getConnection().createStatement();
            ResultSet result = select.executeQuery(query);

            String leftAlignFormat = "| %-4s | %-25s | %-8s | %-25s | %-25s |%n";

            System.out.format("%n+----+---------------------------+----------+---------------------------+---------------------------+%n");
            System.out.format("| ID   | Username                  | Blocked  | TimeOfBlock               | LastPasswordChange        |%n");
            System.out.format("+------+---------------------------+----------+---------------------------+---------------------------+%n");
            while (result.next()) {
                System.out.format(leftAlignFormat, result.getString(1), result.getString(2), result.getString(4),
                        result.getString(5), result.getString(6));
            }
            System.out.format("+------+---------------------------+----------+---------------------------+---------------------------+%n");


        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    private static void showHelp() throws IOException {
        System.out.println("\n*** Help ***");
        System.out.println("System of delimitation of users' rights on top of password authentication. " +
                "\nCreated by the student of IPT, group FB-31, Roman Horilyi." +
                "\nVariant №4: password restriction - only Latin and Cyrillic symbols." +
                "\nCopyright © 2016");
    }

    public static void enterAsUser() throws IOException {
        connect();
        unblockUsers();

        // if there is no users in table - create ADMIN with password ""
        if (isTableEmpty()) {
            Admin admin = new Admin("");
            admin.addUserToDB();
            System.out.println("Successfully login as ADMIN!" +
                    "\nOwing to the fact that it's the first initialisation of the system, " +
                    "you MUST change ADMIN's password.");
            admin.changePasswordFirstAdminEntry();
            showMenu(admin);

        } else {
            System.out.print("\nEnter username > ");
            Scanner sc = new Scanner(System.in);
            StringBuilder username = new StringBuilder();
            username.append(sc.nextLine());

            // check if we are trying to enter as ADMIN
            if ((username.toString()).equals("ADMIN")) {
                System.out.print("Enter password > ");
                Scanner sc1 = new Scanner(System.in);
                StringBuilder password = new StringBuilder();
                password.append(sc1.nextLine());
                int attemptCounter = 0;

                while (!checkPasswordWithDB(username.toString(), password.toString()) && attemptCounter < 3) {
                    System.out.println("\nError. Incorrect password.");
                    System.out.print("Please try again > ");
                    attemptCounter++;
                    sc1 = new Scanner(System.in);
                    password.replace(0, password.length(), sc1.nextLine());
                }

                if (attemptCounter == 3) {
                    exit();
                    return;
                }

                Admin admin = new Admin(password.toString());
                System.out.println("You entered successfully as ADMIN!");
                showMenu(admin);

            } else {
                while (!checkIfUserExists(username.toString())) {
                    System.out.println("\nError. There is no user with such username.");
                    System.out.print("Please try again > ");
                    Scanner sc1 = new Scanner(System.in);
                    username.replace(0, username.length(), sc1.nextLine());
                }

                System.out.print("Enter password > ");
                Scanner sc1 = new Scanner(System.in);
                StringBuilder password = new StringBuilder();
                password.append(sc1.nextLine());

                int attemptCounter = 0;
                // Max amount of attempts - 3, after the 3rd incorrect attempt - block this user and return to main menu
                while (!checkPasswordWithDB(username.toString(), password.toString()) && attemptCounter < 2) {
                    attemptCounter++;
                    System.out.println("\nError. Incorrect password.");
                    System.out.print("Please try again > ");
                    sc1 = new Scanner(System.in);
                    password.replace(0, password.length(), sc1.nextLine());
                }

                if (attemptCounter == 3) {
                    blockUser(username.toString());
                    showMenu();
                }

                if (checkIfBlocked(username.toString())) {
                    System.out.println("This user is blocked due to many attempts to enter a correct password.");
                    showMenu();

                } else {
                    User user = new User(username.toString(), password.toString());
                    System.out.println("You entered successfully as " + username + "!");
                    showMenu(user);
                }
            }
        }
    }

    public static boolean isTableEmpty() {
        boolean ifEmpty = false;
        try {
            Statement select = getConnection().createStatement();
            String query = "SELECT * FROM users";
            ResultSet result = select.executeQuery(query);
            if (!result.next()) {
                ifEmpty = true;
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return ifEmpty;
    }

    public static boolean checkIfUserExists(String username) {
        boolean ifExists = false;

        try {
            String query = "SELECT Username FROM users WHERE Username = ?";
            PreparedStatement select = getConnection().prepareStatement(query);
            select.setString(1, username);

            ResultSet result = select.executeQuery();
            if (result.next()) {
                ifExists = true;
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return ifExists;
    }

    public static boolean checkIfBlocked(String username) {
        boolean ifBlocked = false;

        try {
            String query = "SELECT Blocked FROM users WHERE Username = ? AND Blocked = TRUE";
            PreparedStatement select = getConnection().prepareStatement(query);
            select.setString(1, username);

            ResultSet result = select.executeQuery();
            if (result.next()) {
                ifBlocked = true;
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        return ifBlocked;
    }

    public static boolean checkPasswordWithDB(String username, String password) {
        boolean correctPassword = false;

        try {
            String query = "SELECT * FROM users WHERE Username = ? AND Password = ?";
            PreparedStatement select = getConnection().prepareStatement(query);
            select.setString(1, username);
            select.setString(2, password);

            ResultSet result = select.executeQuery();
            if (result.next()) {
                correctPassword = true;
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        return correctPassword;
    }

    public static void blockUser(String username) {
        try {
            String query = "UPDATE users SET Blocked = TRUE, TimeOfBlock = NOW() WHERE Username = ?";
            PreparedStatement update = getConnection().prepareStatement(query);
            update.setString(1, username);

            update.executeUpdate();
            System.out.println("This user is blocked.");
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public static void unblockUsers() {
        try {
            String query = "UPDATE users SET Blocked = FALSE, TimeOfBlock = NULL WHERE Blocked = TRUE " +
                    "AND TIMEDIFF(NOW(), TimeOfBlock) > '24:00:00.000000'";
            Statement update = getConnection().createStatement();
            update.executeUpdate(query);

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public static void exit() {
        if (getConnection() != null) {
            disconnect();
            System.out.println("\nGoodbye!");
            System.exit(0);
        }
    }
}