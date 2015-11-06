package ru.entel.datadealer.devices;

public class Binding {
    private String masterName;
    private String slaveName;
    private int regNumb;

    public Binding(String masterName, String slaveName, int regNumb) {
        this.slaveName = slaveName;
        this.regNumb = regNumb;
        this.masterName = masterName;
    }

    public String getMasterName() {
        return masterName;
    }

    public String getRegID() {
        return this.slaveName + ":" + this.regNumb;
    }

    public String getChannelID() {
        return this.masterName + ":" + this.slaveName;
    }

    public String getSlaveName() {
        return slaveName;
    }

    public int getRegNumb() {
        return regNumb;
    }

    @Override
    public String toString() {
        return "Binding{" +
                "masterName='" + masterName + '\'' +
                ", slaveName='" + slaveName + '\'' +
                ", regNumb=" + regNumb +
                '}';
    }
}