package ru.entel.smiu.datadealer.db.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс DeviceBlank - класс-сущность, хранит в себе описание шаблона устройства
 * Необходим для связи с JPA (Java Persistence API)
 * @author Мацепура Артем
 * @version 0.2
 */
@Entity
@Table(name = "device_blank", schema = "", catalog = "smiu")
public class DeviceBlank {
    private int id;
    private String deviceType;
    private String protocolType;
    private Set<DeviceEntity> deviceEntities = new HashSet<>(0);

    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "device_type", nullable = false, insertable = true, updatable = true, length = 45)
    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "deviceBlank")
    public Set<DeviceEntity> getDeviceEntities() {
        return deviceEntities;
    }

    public void setDeviceEntities(Set<DeviceEntity> deviceEntities) {
        this.deviceEntities = deviceEntities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceBlank that = (DeviceBlank) o;

        if (id != that.id) return false;
        if (deviceType != null ? !deviceType.equals(that.deviceType) : that.deviceType != null) return false;
        return !(protocolType != null ? !protocolType.equals(that.protocolType) : that.protocolType != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (deviceType != null ? deviceType.hashCode() : 0);
        result = 31 * result + (protocolType != null ? protocolType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DeviceBlank{" +
                ", protocolType='" + protocolType + '\'' +
                ", deviceType='" + deviceType + '\'' +
                '}';
    }
}
