package ru.entel.datadealer.db.entity;

import org.hibernate.FetchMode;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by farades on 06.11.15.
 */
@Entity
@Table(name = "protocol", schema = "", catalog = "smiu")
public class Protocol {
    private int id;
    private String protocolSettings;
    private String name;
    private String type;
    private Set<Device> devices = new HashSet<>(0);


    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "protocol_settings", nullable = false, insertable = true, updatable = true, length = 65535)
    public String getProtocolSettings() {
        return protocolSettings;
    }

    public void setProtocolSettings(String protocolSettings) {
        this.protocolSettings = protocolSettings;
    }

    @Basic
    @Column(name = "name", nullable = false, insertable = true, updatable = true, length = 45)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "protocol")
    public Set<Device> getDevices() {
        return devices;
    }

    public void setDevices(Set<Device> devices) {
        this.devices = devices;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Protocol protocol = (Protocol) o;

        if (id != protocol.id) return false;
        if (protocolSettings != null ? !protocolSettings.equals(protocol.protocolSettings) : protocol.protocolSettings != null)
            return false;
        if (name != null ? !name.equals(protocol.name) : protocol.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (protocolSettings != null ? protocolSettings.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Basic
    @Column(name = "type", nullable = false, insertable = true, updatable = true, length = 45)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Protocol{" +
                "protocolSettings='" + protocolSettings + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", devices=" + devices +
                '}';
    }
}
