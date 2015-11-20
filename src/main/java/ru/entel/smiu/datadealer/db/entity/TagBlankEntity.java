package ru.entel.smiu.datadealer.db.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "tag_blank", schema = "", catalog = "smiu")
public class TagBlankEntity {
    private int id;
    private String tagDescr;
    private String tagName;
    private Set<Tag> tags;
    private Set<AlarmBlank> alarmBlanks;
    private String tagBinding;
    private DeviceEntity deviceEntity;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "device_id")
    public DeviceEntity getDeviceEntity() {
        return deviceEntity;
    }

    public void setDeviceEntity(DeviceEntity deviceEntity) {
        this.deviceEntity = deviceEntity;
    }

    @Basic
    @Column(name = "tag_name", nullable = false, insertable = true, updatable = true, length = 100)
    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tagBlankEntity")
    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "tagBlankEntity")
    public Set<AlarmBlank> getAlarmBlanks() {
        return alarmBlanks;
    }

    public void setAlarmBlanks(Set<AlarmBlank> alarmBlanks) {
        this.alarmBlanks = alarmBlanks;
    }

    @Basic
    @Column(name = "tag_binding")
    public String getTagBinding() {
        return tagBinding;
    }

    public void setTagBinding(String tagBinding) {
        this.tagBinding = tagBinding;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TagBlankEntity that = (TagBlankEntity) o;

        if (id != that.id) return false;
        if (tagName != null ? !tagName.equals(that.tagName) : that.tagName != null) return false;
        return !(tagBinding != null ? !tagBinding.equals(that.tagBinding) : that.tagBinding != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (tagName != null ? tagName.hashCode() : 0);
        result = 31 * result + (tagBinding != null ? tagBinding.hashCode() : 0);
        return result;
    }
}
