package ru.entel.smiu.datadealer.db.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс ProtocolEntity - класс-сущность, хранит в себе один физический протокол для опроса.
 * Агрегирует в себя дочерние экземпляры устройств из которых берет необходимые параметры
 * для их опроса.
 * Необходим для связи с JPA (Java Persistence API)
 * @author Мацепура Артем
 * @version 0.2
 */
@Entity
@Table(name = "protocol", schema = "", catalog = "smiu")
public class ProtocolEntity {
    private int id;
    private String protocolSettings;
    private String name;
    private String type;
    private Set<ChannelEntity> channelEntities = new HashSet<>(0);


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

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "protocolEntity")
    public Set<ChannelEntity> getChannelEntities() {
        return channelEntities;
    }

    public void setChannelEntities(Set<ChannelEntity> channelEntities) {
        this.channelEntities = channelEntities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProtocolEntity protocolEntity = (ProtocolEntity) o;

        if (id != protocolEntity.id) return false;
        if (protocolSettings != null ? !protocolSettings.equals(protocolEntity.protocolSettings) : protocolEntity.protocolSettings != null)
            return false;
        if (name != null ? !name.equals(protocolEntity.name) : protocolEntity.name != null) return false;

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
        return "ProtocolEntity{" +
                "protocolSettings='" + protocolSettings + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
