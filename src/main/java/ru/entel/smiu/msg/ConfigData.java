package ru.entel.smiu.msg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConfigData {
    private Map<String, String> properties = new HashMap<>();
    private Set<DeviceConfPackage> devices = new HashSet<>();

    public ConfigData(Map<String, String> properties, Set<DeviceConfPackage> devices) {
        this.properties = properties;
        this.devices = devices;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public Set<DeviceConfPackage> getDevices() {
        return devices;
    }

    public void setDevices(Set<DeviceConfPackage> devices) {
        this.devices = devices;
    }
}
