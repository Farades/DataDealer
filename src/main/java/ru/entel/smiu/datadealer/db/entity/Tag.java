package ru.entel.smiu.datadealer.db.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Класс Tag - класс-сущность, хранит в себе один тэг.
 * Тэг представляет из себя композицию значения, ссылки на шаблон тэга и временной отметки.
 * Необходим для связи с JPA (Java Persistence API)
 * @author Мацепура Артем
 * @version 0.2
 */
@Entity
@Table(name = "tag", schema = "", catalog = "smiu")
public class Tag {
    private int id;
    private String value;

    @Temporal(TemporalType.TIMESTAMP)
    private Date tagTime;

    private DeviceEntity deviceEntity;
    private TagBlankEntity tagBlankEntity;

    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    @GenericGenerator(name="kaugen" , strategy="increment")
    @GeneratedValue(generator="kaugen")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "value", nullable = true, insertable = true, updatable = true, length = 45)
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Basic
    @Column(name = "tag_time", nullable = true, insertable = true, updatable = true)
    public Date getTagTime() {
        return tagTime;
    }

    public void setTagTime(Date tagTime) {
        this.tagTime = tagTime;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "device_id")
    public DeviceEntity getDeviceEntity() {
        return deviceEntity;
    }

    public void setDeviceEntity(DeviceEntity deviceEntity) {
        this.deviceEntity = deviceEntity;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tag_blank_id")
    public TagBlankEntity getTagBlankEntity() {
        return tagBlankEntity;
    }

    public void setTagBlankEntity(TagBlankEntity tagBlankEntity) {
        this.tagBlankEntity = tagBlankEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag = (Tag) o;

        if (deviceEntity != null ? !deviceEntity.equals(tag.deviceEntity) : tag.deviceEntity != null) return false;
        return !(tagBlankEntity != null ? !tagBlankEntity.equals(tag.tagBlankEntity) : tag.tagBlankEntity != null);

    }

    @Override
    public int hashCode() {
        int result = deviceEntity != null ? deviceEntity.hashCode() : 0;
        result = 31 * result + (tagBlankEntity != null ? tagBlankEntity.hashCode() : 0);
        return result;
    }
}
