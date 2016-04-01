package ua.ipt.signature;

/**
 * Created by Roman Horilyi on 31.03.2016.
 */
public class Signature {
    private String username;
    private String machineHostname;
    private String osName;
    private int mouseButtonsNumber;
    private double displayWidth;
    private long diskCapacity;

    public Signature(String username, String machineHostname, String osName, int mouseButtonsNumber,
                     double displayWidth, long diskCapacity) {
        this.username = username;
        this.machineHostname = machineHostname;
        this.osName = osName;
        this.mouseButtonsNumber = mouseButtonsNumber;
        this.displayWidth = displayWidth;
        this.diskCapacity = diskCapacity;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMachineHostname() {
        return machineHostname;
    }

    public void setMachineHostname(String machineHostname) {
        this.machineHostname = machineHostname;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public int getMouseButtonsNumber() {
        return mouseButtonsNumber;
    }

    public void setMouseButtonsNumber(int mouseButtonsNumber) {
        this.mouseButtonsNumber = mouseButtonsNumber;
    }

    public double getDisplayWidth() {
        return displayWidth;
    }

    public void setDisplayWidth(double displayWidth) {
        this.displayWidth = displayWidth;
    }

    public long getDiskCapacity() {
        return diskCapacity;
    }

    public void setDiskCapacity(long diskCapacity) {
        this.diskCapacity = diskCapacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Signature signature = (Signature) o;

        if (mouseButtonsNumber != signature.mouseButtonsNumber) return false;
        if (Double.compare(signature.displayWidth, displayWidth) != 0) return false;
        if (diskCapacity != signature.diskCapacity) return false;
        if (!username.equals(signature.username)) return false;
        if (!machineHostname.equals(signature.machineHostname)) return false;
        return osName.equals(signature.osName);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = username.hashCode();
        result = 31 * result + machineHostname.hashCode();
        result = 31 * result + osName.hashCode();
        result = 31 * result + mouseButtonsNumber;
        temp = Double.doubleToLongBits(displayWidth);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (diskCapacity ^ (diskCapacity >>> 32));
        return result;
    }
}
