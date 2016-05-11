package ua.ipt.validation.hardware;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

/**
 * Created by Roman Horilyi on 10.04.2016.
 */
public class Hardware4Win {
    private static String serialNumber = null;

    public static final String getSerialNumber() {

        if (serialNumber != null) {
            return serialNumber;
        }

        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            process = runtime.exec(new String[]{"wmic", "bios", "get", "serialnumber"});
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (process != null) {
            try (OutputStream os = process.getOutputStream();
                 InputStream is = process.getInputStream()
            ) {

                Scanner sc = new Scanner(is);
                while (sc.hasNext()) {
                    String next = sc.next();
                    if ("SerialNumber".equals(next)) {
                        serialNumber = sc.next().trim();
                        break;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (serialNumber == null) {
            throw new RuntimeException("Cannot find computer SN");
        }
        return serialNumber;
    }
}
