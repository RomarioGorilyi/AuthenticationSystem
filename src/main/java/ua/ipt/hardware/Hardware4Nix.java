package ua.ipt.hardware;

import java.io.*;

/**
 * Created by Roman Horilyi on 10.04.2016.
 */
public class Hardware4Nix {
    private static String serialNumber = null;

    public static final String getSerialNumber() {
        if (serialNumber == null) {
            readDmidecode();
        }
        if (serialNumber == null) {
            readLshal();
        }
        if (serialNumber == null) {
            throw new RuntimeException("Cannot find computer SN");
        }

        return serialNumber;
    }

    private static BufferedReader read(String command) {
        OutputStream os;
        InputStream is;

        Runtime runtime = Runtime.getRuntime();
        Process process;
        try {
            process = runtime.exec(command.split(" "));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        os = process.getOutputStream();
        is = process.getInputStream();

        try {
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new BufferedReader(new InputStreamReader(is));
    }

    private static void readDmidecode() {
        String line;
        String marker = "Serial Number:";

        try (BufferedReader br = read("dmidecode -t system")) {
            while ((line = br.readLine()) != null) {
                if (line.contains(marker)) {
                    serialNumber = line.split(marker)[1].trim();
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void readLshal() {
        String line;
        String marker = "system.hardware.serial =";

        try (BufferedReader br = read("lshal")) {
            while ((line = br.readLine()) != null) {
                if (line.contains(marker)) {
                    serialNumber = line.split(marker)[1].replaceAll("\\(string\\)|(\\')", "").trim();
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
