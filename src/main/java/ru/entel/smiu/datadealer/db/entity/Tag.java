package ru.entel.smiu.datadealer.db.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by farades on 07.11.15.
 */
@Entity
@Table(name = "tag", schema = "", catalog = "smiu")
public class Tag {
    private int id;
    private String value;

    @Temporal(TemporalType.TIMESTAMP)
    private Date tagTime;

    private Device device;
    private TagBlank tagBlank;

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
    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tag_blank_id")
    public TagBlank getTagBlank() {
        return tagBlank;
    }

    public void setTagBlank(TagBlank tagBlank) {
        this.tagBlank = tagBlank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag = (Tag) o;

        if (device != null ? !device.equals(tag.device) : tag.device != null) return false;
        return !(tagBlank != null ? !tagBlank.equals(tag.tagBlank) : tag.tagBlank != null);

    }

    @Override
    public int hashCode() {
        int result = device != null ? device.hashCode() : 0;
        result = 31 * result + (tagBlank != null ? tagBlank.hashCode() : 0);
        return result;
    }
}
