package ua.ipt.hardware;

import java.io.*;

/**
 * Created by Roman Horilyi on 10.04.2016.
 */
public class Hardware4Mac {
    private static String serialNumber = null;

    public static final String getSerialNumber() {

        if (serialNumber != null) {
            return serialNumber;
        }

        Runtime runtime = Runtime.getRuntime();
        Process process;
        try {
            process = runtime.exec(new String[] { "/usr/sbin/system_profiler", "SPHardwareDataType" });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (OutputStream os = process.getOutputStream();
             InputStream is = process.getInputStream()
        ) {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            String marker = "Serial Number";

            while ((line = br.readLine()) != null) {
                if (line.contains(marker)) {
                    serialNumber = line.split(":")[1].trim();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (serialNumber == null) {
            throw new RuntimeException("Cannot find computer SN");
        }

        return serialNumber;
    }
}
