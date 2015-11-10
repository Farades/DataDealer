package ru.entel.smiu.datadealer.db.entity;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by farades on 06.11.15.
 */
@Entity
@Table(name = "device", schema = "", catalog = "smiu")
public class Device {
    private int id;
    private String deviceSettings;
    private String name;
    private Protocol protocol;
    private DeviceBlank deviceBlank;
    private Set<Tag> tags;

    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "device_settings", nullable = false, insertable = true, updatable = true, length = 65535)
    public String getDeviceSettings() {
        return deviceSettings;
    }

    public void setDeviceSettings(String deviceSettings) {
        this.deviceSettings = deviceSettings;
    }

    @Basic
    @Column(name = "name", nullable = false, insertable = true, updatable = true, length = 45)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "protocol_id")
    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "device_blank_id")
    public DeviceBlank getDeviceBlank() {
        return deviceBlank;
    }

    public void setDeviceBlank(DeviceBlank deviceBlank) {
        this.deviceBlank = deviceBlank;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "device")
    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Device device = (Device) o;

        if (id != device.id) return false;
        if (deviceSettings != null ? !deviceSettings.equals(device.deviceSettings) : device.deviceSettings != null)
            return false;
        return !(name != null ? !name.equals(device.name) : device.name != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (deviceSettings != null ? deviceSettings.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Device{" +
                "deviceBlank=" + deviceBlank +
                ", name='" + name + '\'' +
                ", deviceSettings='" + deviceSettings + '\'' +
                '}';
    }
}
