package ru.entel.datadealer.db.entity;

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

        if (id != tag.id) return false;
        if (value != null ? !value.equals(tag.value) : tag.value != null) return false;
        if (tagTime != null ? !tagTime.equals(tag.tagTime) : tag.tagTime != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (tagTime != null ? tagTime.hashCode() : 0);
        return result;
    }
}
