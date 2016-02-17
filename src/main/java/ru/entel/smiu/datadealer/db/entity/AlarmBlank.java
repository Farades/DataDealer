package ru.entel.smiu.datadealer.db.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс AlarmBlank - класс-сущность, хранит в себе одну возможную аварию.
 * Необходим для связи с JPA (Java Persistence API)
 * @author Мацепура Артем
 * @version 0.2
 */
@Entity
@Table(name = "alarm_blank", schema = "", catalog = "smiu")
public class AlarmBlank {
    private int id;
    private String condition;
    private String description;

    private TagBlankEntity tagBlankEntity;

    private Set<AlarmEntity> alarmEntitySet = new HashSet<>(0);

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "condition")
    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    @Basic
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tag_blank_id")
    public TagBlankEntity getTagBlankEntity() {
        return tagBlankEntity;
    }

    public void setTagBlankEntity(TagBlankEntity tagBlankEntity) {
        this.tagBlankEntity = tagBlankEntity;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "alarmBlank")
    public Set<AlarmEntity> getAlarmEntitySet() {
        return alarmEntitySet;
    }

    public void setAlarmEntitySet(Set<AlarmEntity> alarmEntitySet) {
        this.alarmEntitySet = alarmEntitySet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AlarmBlank that = (AlarmBlank) o;

        if (id != that.id) return false;
        return !(tagBlankEntity != null ? !tagBlankEntity.equals(that.tagBlankEntity) : that.tagBlankEntity != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (tagBlankEntity != null ? tagBlankEntity.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AlarmBlank{" +
                "condition='" + condition + '\'' +
                '}';
    }
}
