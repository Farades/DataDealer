package ru.entel.smiu.datadealer.db.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * Класс DeviceBlank - класс-сущность, хранит в себе один конкретный экземпляр устройства.
 * Необходим для связи с JPA (Java Persistence API)
 * @author Мацепура Артем
 * @version 0.2
 */
@Entity
@Table(name = "device", schema = "", catalog = "smiu")
public class DeviceEntity implements Serializable {
    private int id;
    private String name;
    private DeviceBlank deviceBlank;
    private Set<Tag> tags;
    private Set<TagBlankEntity> tagBlankEntities;
    private String systemName;

    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
    @JoinColumn(name = "device_blank_id")
    public DeviceBlank getDeviceBlank() {
        return deviceBlank;
    }

    public void setDeviceBlank(DeviceBlank deviceBlank) {
        this.deviceBlank = deviceBlank;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "deviceEntity")
    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "deviceEntity")
    public Set<TagBlankEntity> getTagBlankEntities() {
        return tagBlankEntities;
    }

    public void setTagBlankEntities(Set<TagBlankEntity> tagBlankEntities) {
        this.tagBlankEntities = tagBlankEntities;
    }

    @Basic
    @Column(name = "system_name")
    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }
}
