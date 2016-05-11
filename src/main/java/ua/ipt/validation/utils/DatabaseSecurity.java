package ua.ipt.validation.utils;

import ua.ipt.exceptions.CryptoException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import static ua.ipt.database.Connector.getConnection;


/**
 * Created by Roman Horilyi on 19.04.2016.
 */
public class DatabaseSecurity {

    private static String systemPassword = null;
    private static final String passwordFileAddress =
            "C://IdeaProjects/SecurityOfSoftware/Lab1/src/main/resources/password.txt";
    private static final String encryptedTableAddress =
            "C://IdeaProjects/SecurityOfSoftware/Lab1/src/main/resources/encryptedUsers.csv";
    private static final String decryptedTableAddress =
            "C://IdeaProjects/SecurityOfSoftware/Lab1/src/main/resources/decryptedUsers.csv";
    private static final String ALGORITHM = "RC4";
    private static final String TRANSFORMATION = "RC4";

    public static boolean checkSystemPassword() {
        String passwordToCheck = readPasswordFromFile(passwordFileAddress);

        int attemptsCounter = 0;
        while (attemptsCounter < 3) {
            System.out.print("\nPlease, enter password to get access to the system > ");
            Scanner scanner = new Scanner(System.in);
            StringBuilder password = new StringBuilder();
            password.replace(0, password.length(), scanner.nextLine());

            String encryptedPassword = encryptPasswordMD5(password.toString());

            if (passwordToCheck.equals(encryptedPassword)) {
                System.out.println("Correct password.\n");
                systemPassword = password.toString();
                return true;

            } else {
                System.out.println("Incorrect password.");
                attemptsCounter++;
            }
        }

        return false;
    }

    public static void getNewSystemPassword() {
        System.out.print("\nPlease, enter new system password > ");
        Scanner scanner = new Scanner(System.in);
        StringBuilder newPassword = new StringBuilder();
        newPassword.replace(0, newPassword.length(), scanner.nextLine());

        while (newPassword.equals(systemPassword) || newPassword.length() < 5 || newPassword.length() > 128) {
            System.out.print("\nYou have to choose such a password which is different from the existing one. " +
                    "Try again > ");
            newPassword.replace(0, newPassword.length(), scanner.nextLine());
        }

        String encryptedPassword = encryptPasswordMD5(newPassword.toString());

        writeFile(encryptedPassword, passwordFileAddress);
        systemPassword = newPassword.toString();
        System.out.println("\nPassword was changed successfully.");
    }

    public static String readPasswordFromFile(String address) {
        String fileData = null;

        File file = new File(address);
        try (Scanner scanner = new Scanner(file)) {
            fileData = scanner.nextLine();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return fileData;
    }

    public static void writeFile(String data, String address) {
        try (FileOutputStream stream = new FileOutputStream(address);
             OutputStreamWriter file = new OutputStreamWriter(stream)
        ) {
            file.write(data);

        } catch (IOException e) {
            System.out.println("Can't write the file");
        }
    }

    private static String encryptPasswordMD5(String password) {
        String encryptedPassword = null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            // Message digests are secure one-way hash functions that take arbitrary-sized data and
            // output a fixed-length hash value.
            md.update(password.getBytes());
            byte[] digest = md.digest(); // Completes the hash computation
            StringBuilder sb = new StringBuilder();
            for (byte b: digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            encryptedPassword = sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return  encryptedPassword;
    }

    public static void importUsersTable() throws CryptoException {
        try {
            File encryptedFile = new File(encryptedTableAddress);
            File outputDecryptedFile = new File(decryptedTableAddress);

            decryptFile(systemPassword, encryptedFile, outputDecryptedFile);

            Path encryptedPath = Paths.get(encryptedTableAddress);
            Files.delete(encryptedPath);

            Statement statement = getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "LOAD DATA INFILE '" + decryptedTableAddress + "' REPLACE INTO TABLE users FIELDS " +
                    "TERMINATED BY ','";
            statement.executeUpdate(query);

            Path decryptedPath = Paths.get(decryptedTableAddress);
            Files.delete(decryptedPath);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        System.out.println("Table with users was imported successfully.\n");
    }

    public static void exportUsersTable() throws CryptoException {
        try {
            Statement statement = getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT IDUser, Username, Password, Blocked, TimeOfBlock, LastPasswordChange into OUTFILE '"
                    + decryptedTableAddress + "' FIELDS TERMINATED BY ',' FROM users";

            statement.executeQuery(query);

            File inputFile = new File(decryptedTableAddress);
            File outputEncryptedFile = new File(encryptedTableAddress);
            encryptFile(systemPassword, inputFile, outputEncryptedFile);

            Path decryptedPath = Paths.get(decryptedTableAddress);
            Files.delete(decryptedPath);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        System.out.println("Table with users was exported successfully.");
    }

    public static void cleanUsersTable() {
        try {
            Statement statement = getConnection().createStatement();
            String query = "delete from users";
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void encryptFile(String key, File inputFile, File outputFile)
            throws CryptoException {
        doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }

    private static void decryptFile(String key, File inputFile, File outputFile)
            throws CryptoException {
        doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }

    private static void doCrypto(int cipherMode, String key, File inputFile, File outputFile)
            throws CryptoException {
        try (FileInputStream inputStream = new FileInputStream(inputFile);
             FileOutputStream outputStream = new FileOutputStream(outputFile)
        ) {
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);

            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);

            byte[] outputBytes = cipher.doFinal(inputBytes);

            outputStream.write(outputBytes);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException
                | BadPaddingException | IllegalBlockSizeException e) {
            throw new CryptoException("Error encrypting/decrypting file", e);
        }
    }
}