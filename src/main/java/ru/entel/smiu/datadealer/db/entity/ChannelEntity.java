package ru.entel.smiu.datadealer.db.entity;

import javax.persistence.*;

/**
 * Класс ChannelEntity - класс-сущность, хранит в описание одного канала опроса
 * Необходим для связи с JPA (Java Persistence API)
 * @author Мацепура Артем
 * @version 0.2
 */
@Entity
@Table(name = "channel", schema = "", catalog = "smiu")
public class ChannelEntity {
    private int id;
    private String settings;
    private String name;
    private ProtocolEntity protocolEntity;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "settings")
    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "protocol_id")
    public ProtocolEntity getProtocolEntity() {
        return protocolEntity;
    }

    public void setProtocolEntity(ProtocolEntity protocolEntity) {
        this.protocolEntity = protocolEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChannelEntity channelEntity = (ChannelEntity) o;

        if (id != channelEntity.id) return false;
        if (settings != null ? !settings.equals(channelEntity.settings) : channelEntity.settings != null) return false;
        if (name != null ? !name.equals(channelEntity.name) : channelEntity.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (settings != null ? settings.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
