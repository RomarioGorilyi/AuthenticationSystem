package ua.ipt.main;

import ua.ipt.hardware.Hardware;
import ua.ipt.users.Admin;
import ua.ipt.users.User;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import static ua.ipt.database.Connector.*;


/**
 * Created by Roman Horilyi on 18.03.2016.
 */
public class MyApp {

    public static void main(String[] args) throws IOException {
        connect();
        boolean isLegal = AuthenticationManager.isLegal();
        if (!isLegal) {
            System.out.println("You have no rights for this program.");
        } else if (isAppBlocked()) {
            System.out.println("You are blocked!");
        } else {
            System.out.println("----------------------------MyApp----------------------------");
            showMenu();
        }
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
                unblockAppropriateUsers();
                enterSystem();
                break;
            case 2:
                showHelp();
                waitForPressEnter();
                showMenu();
                break;
            case 3:
                exitApp();
                break;
            default: break;
        }
    }

    /**
     * Create scanner that waits for press of the button [Enter] and is used to return to the previous menu.
     */
    public static void waitForPressEnter() {
        System.out.print("\nPress <Enter> for return to the menu > ");
        Scanner readInput = new Scanner(System.in);
        String command = readInput.nextLine();
        while (!command.equals("")) {
            command = readInput.nextLine();
        }
    }

    /**
     * ADMIN menu
     * @param admin ADMIN who enters and works with the system
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
                waitForPressEnter();
                showMenu(admin);
                break;
            case 3:
                admin.addNewUser();
                showMenu(admin);
                break;
            case 4:
                System.out.print("Enter username of user you want to block > ");
                Scanner readInput = new Scanner(System.in);
                StringBuilder username = new StringBuilder();
                username.append(readInput.nextLine());

                while (!checkIfUserExists(username.toString())) {
                    System.out.println("\nError. There is no user with such username.");
                    System.out.print("Please try again > ");
                    username.replace(0, username.length(), sc.nextLine());
                }

                blockUser(username.toString());
                waitForPressEnter();
                showMenu(admin);
                break;
            case 5:
                showHelp();
                waitForPressEnter();
                showMenu(admin);
                break;
            case 6:
                exitApp();
            default: break;
        }
    }

    /**
     * User menu
     * @param user user who enters and works with the system
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
                waitForPressEnter();
                showMenu(user);
                break;
            case 3:
                exitApp();
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

    /**
     * Show Help that gives an about information of this project.
     */
    private static void showHelp() {
        System.out.println("\n*** Help ***");
        System.out.println("System of delimitation of users' rights on top of password authentication. " +
                "\nCreated by the student of IPT, group FB-31, Roman Horilyi." +
                "\nVariant №4: password restriction - only Latin and Cyrillic symbols." +
                "\nCopyright © 2016");
    }

    /**
     * Enter the system validating a username and a corresponding password to this user.
     *
     * If the table that contains all existing users with their flags is empty (it's the 1st entry in the system),
     * create ADMIN with an empty password ("").
     *
     * @throws IOException
     */
    public static void enterSystem() throws IOException {
        if (checkIfTableEmpty()) {
            Admin admin = new Admin("");
            admin.addUserToDB();
            System.out.println("Successfully login as ADMIN!" +
                    "\nOwing to the fact that it's the first initialisation of the system, " +
                    "you MUST change ADMIN's password.");
            admin.changePasswordFirstAdminEntry();
            showMenu(admin);

        } else {
            int attemptCounter = 0; // is used to count unsuccessful attempts of a user to enter the system
            validateUser(attemptCounter);
        }
    }

    public static boolean checkIfTableEmpty() {
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

    /**
     * Validate possible user checking his username, password and rights with database
     *
     * @param attemptCounter number of attempts to enter the system that were made beforehand
     * @return number of overall attempts to enter the system
     * @throws IOException
     */
    public static int validateUser(int attemptCounter) throws IOException {
        if (attemptCounter < 5) {
            System.out.print("\nEnter username > ");
            Scanner sc = new Scanner(System.in);
            StringBuilder username = new StringBuilder();
            username.append(sc.nextLine());

            // check if we are trying to enter as ADMIN
            if (username.toString().equals("ADMIN")) {
                enterAsAdmin(attemptCounter);

            } else if (username.toString().toLowerCase().equals("admin") || !checkIfUserExists(username.toString())) {
                // Due to the fact that MySQL isn't sensitive to letter case we should carry out ADMIN username
                // verification.
                //
                // For example, our program shouldn't except "Admin", "admin", "aDMIN" as a valid username of ADMIN.
                System.out.println("Error. There is no user with such username.");
                validateUser(++attemptCounter);

            } else {
                enterAsUser(username.toString(), attemptCounter);
            }

        } else {
            blockApp();
            exitApp();
        }

        return attemptCounter;
    }

    /**
     * Block usage of the program in case a user fails to validate correctly in the system 5 times updating a record
     * in the table of blocked users in case there is already current user in database or adding new blocked user
     * to the DB.
     */
    private static void blockApp() {
        String serialNumber = Hardware.getSerialNumber();
        if (!updateBlockIfUserExistsInDB(serialNumber)) {
            addBlockedUserToDB(serialNumber);
        }
    }

    /**
     * Add user that fails to validate correctly in the system 5 times to the table of blocked users in database
     * @param serialNumber hashcode of a serial number that contains an information about user computer
     */
    private static void addBlockedUserToDB(String serialNumber) {
        try {
            String query = "INSERT INTO blockedusers(SerialNumber) values(?)";
            PreparedStatement insert = getConnection().prepareStatement(query);
            insert.setString(1, serialNumber);
            insert.executeUpdate();
            System.out.println("\nUnfortunately, you are blocked!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * If there is a record that corresponds to the current user in the table of blocked users, update this existing
     * record renovating TimeOfBlock with relevant time of the last block.
     * Otherwise, we do nothing.
     *
     * @param serialNumber hashcode of a serial number that contains an information about user computer
     * @return TRUE if there is already a user in the DB, otherwise - FALSE
     */
    private static boolean updateBlockIfUserExistsInDB(String serialNumber) {
        boolean ifUserExistsInDB = false;

        try {
            String query = "SELECT * FROM blockedusers WHERE SerialNumber = ?";
            PreparedStatement select = getConnection().prepareStatement(query);
            select.setString(1, serialNumber);
            ResultSet result = select.executeQuery();

            if (result.next()) {
                ifUserExistsInDB = true;
                query = "UPDATE blockedusers SET TimeOfBlock = NOW() WHERE SerialNumber = ?";
                Statement update = getConnection().createStatement();
                update.executeUpdate(query);
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        return ifUserExistsInDB;
    }

    /**
     * Check if program is blocked for the current user
     * (check if there is a user in the table of blocked users and at the same time 24 hours of block haven't expired)
     *
     * @return TRUE if program is blocked to the current user, otherwise - FALSE
     */
    private static boolean isAppBlocked() {
        boolean ifBlocked = false;
        String serialNumber = Hardware.getSerialNumber();
        try {
            String query = "SELECT * FROM blockedusers WHERE SerialNumber = ? " +
                    "AND TIMEDIFF(NOW(), TimeOfBlock) <= '24:00:00.000000'";
            PreparedStatement select = getConnection().prepareStatement(query);
            select.setString(1, serialNumber);

            ResultSet result = select.executeQuery();
            if (result.next()) {
                ifBlocked = true;
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return ifBlocked;
    }

    /**
     * Enter the system as ADMIN, if password is valid. Otherwise, try validate user once more.
     *
     * @param attemptCounter number of attempts to enter the system that were made beforehand
     * @return number of overall attempts to enter the system
     * @throws IOException
     */
    public static int enterAsAdmin(int attemptCounter) throws IOException {
        System.out.print("Enter password > ");
        Scanner sc1 = new Scanner(System.in);
        StringBuilder password = new StringBuilder();
        password.append(sc1.nextLine());

        if (!checkPasswordWithDB("ADMIN", password.toString())) {
            System.out.println("Error. Incorrect password.");
            validateUser(++attemptCounter);

        } else {
            Admin admin = new Admin(password.toString());
            System.out.println("You entered successfully as ADMIN!");
            showMenu(admin);
        }

        return attemptCounter;
    }

    /**
     * Enter the system as simple user, if password is valid and user isn't blocked.
     * Otherwise, try validate user once more.
     *
     * @param username username of the user who tends to enter the system
     * @param attemptCounter number of attempts to enter the system that were made beforehand
     * @throws IOException
     */
    public static void enterAsUser(String username, int attemptCounter) throws IOException {
        System.out.print("Enter password > ");
        Scanner sc1 = new Scanner(System.in);
        StringBuilder password = new StringBuilder();
        password.append(sc1.nextLine());

        if (!checkPasswordWithDB(username, password.toString())) {
            System.out.println("\nError. Incorrect password.");
            attemptCounter++;
            validateUser(attemptCounter);

        } else {
            if (checkIfBlocked(username)) {
                System.out.println("This user is blocked due to many attempts to enter a correct password.");
                showMenu();

            } else {
                User user = new User(username, password.toString());
                System.out.println("You entered successfully as " + username + "!");
                showMenu(user);
            }
        }
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

    /**
     * Check if password corresponds to the user in database.
     *
     * @param username username of user to be checked in DB
     * @param password password that is compared with an actual password of the user
     * @return TRUE if password is correct, FALSE - if not
     */
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

    /**
     * Unblock all users whose 24 hours of blocking their account.
     */
    public static void unblockAppropriateUsers() {
        try {
            String query = "UPDATE users SET Blocked = FALSE, TimeOfBlock = NULL WHERE Blocked = TRUE " +
                    "AND TIMEDIFF(NOW(), TimeOfBlock) > '24:00:00.000000'";
            Statement update = getConnection().createStatement();
            update.executeUpdate(query);

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public static void exitApp() {
        if (getConnection() != null) {
            disconnect();
        }
        System.out.println("\nGoodbye!");
        System.exit(0);
    }
}