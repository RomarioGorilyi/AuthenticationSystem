package ua.ipt.hardware;

import org.apache.commons.lang3.SystemUtils;

/**
 * Created by Roman Horilyi on 10.04.2016.
 */
public class Hardware {
    public static final String getSerialNumber() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return Hardware4Win.getSerialNumber();
        }
        if (SystemUtils.IS_OS_LINUX) {
            return Hardware4Nix.getSerialNumber();
        }
        if (SystemUtils.IS_OS_MAC_OSX) {
            return Hardware4Mac.getSerialNumber();
        }
        return null;
    }
}
