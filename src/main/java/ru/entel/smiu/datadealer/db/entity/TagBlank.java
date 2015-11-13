package ru.entel.smiu.datadealer.db.entity;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by farades on 06.11.15.
 */
@Entity
@Table(name = "tag_blank", schema = "", catalog = "smiu")
public class TagBlank {
    private int id;
    private String tagDescr;
    private String tagName;
    private String tagId;
    private int delay;
    private DeviceBlank deviceBlank;
    private Set<Tag> tags;
    private Set<AlarmBlank> alarmBlanks;

    private Integer selectedId;

    @Transient
    public Integer getSelectedId() {
        return selectedId;
    }

    public void setSelectedId(Integer selectedId) {
        this.selectedId = selectedId;
    }

    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "tag_descr", nullable = true, insertable = true, updatable = true, length = 45)
    public String getTagDescr() {
        return tagDescr;
    }

    public void setTagDescr(String tagDescr) {
        this.tagDescr = tagDescr;
    }

    @Basic
    @Column(name = "tag_name", nullable = false, insertable = true, updatable = true, length = 100)
    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    @Basic
    @Column(name = "tag_id", nullable = false, insertable = true, updatable = true, length = 45)
    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    @Basic
    @Column(name = "delay", nullable = false, insertable = true, updatable = true)
    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "device_blank_id")
    public DeviceBlank getDeviceBlank() {
        return deviceBlank;
    }

    public void setDeviceBlank(DeviceBlank deviceBlank) {
        this.deviceBlank = deviceBlank;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tagBlank")
    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "tagBlank")
    public Set<AlarmBlank> getAlarmBlanks() {
        return alarmBlanks;
    }

    public void setAlarmBlanks(Set<AlarmBlank> alarmBlanks) {
        this.alarmBlanks = alarmBlanks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TagBlank tagBlank = (TagBlank) o;

        if (id != tagBlank.id) return false;
        if (delay != tagBlank.delay) return false;
        if (tagDescr != null ? !tagDescr.equals(tagBlank.tagDescr) : tagBlank.tagDescr != null) return false;
        if (tagName != null ? !tagName.equals(tagBlank.tagName) : tagBlank.tagName != null) return false;
        if (tagId != null ? !tagId.equals(tagBlank.tagId) : tagBlank.tagId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (tagDescr != null ? tagDescr.hashCode() : 0);
        result = 31 * result + (tagName != null ? tagName.hashCode() : 0);
        result = 31 * result + (tagId != null ? tagId.hashCode() : 0);
        result = 31 * result + delay;
        return result;
    }

    @Override
    public String toString() {
        return "TagBlank{" +
                "tagDescr='" + tagDescr + '\'' +
                ", tagName='" + tagName + '\'' +
                ", tagId='" + tagId + '\'' +
                ", delay=" + delay +
                '}';
    }
}
