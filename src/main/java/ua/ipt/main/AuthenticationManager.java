package ua.ipt.main;

import ua.ipt.signature.Signature;
import ua.ipt.signature.WinRegistry;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Roman Horilyi on 31.03.2016.
 */
public class AuthenticationManager {
    private static int hashCode;

    static {
        try {
            String valueOfSignature = WinRegistry.readString(
                    WinRegistry.HKEY_CURRENT_USER,              // HKEY
                    "SOFTWARE\\JetBrains\\IntelliJ IDEA",       // Key
                    "Roman_Horilyi");                           // ValueName
            hashCode = Integer.parseInt(valueOfSignature);

        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static boolean isLegal() throws IOException {
        String username = System.getProperty("user.name");

        java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
        String machineHostname = localMachine.getHostName();

        String osName = System.getProperty("os.name");

        int mouseButtonsNumber = java.awt.MouseInfo.getNumberOfButtons();

        double displayWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();

        File[] roots = File.listRoots();
        long diskCapacity = roots[1].getTotalSpace(); // D disk

        Signature signature = new Signature(username, machineHostname, osName, mouseButtonsNumber, displayWidth,
                diskCapacity);

        return signature.hashCode() == hashCode;
    }
}
