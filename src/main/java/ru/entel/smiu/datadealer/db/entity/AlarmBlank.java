package ru.entel.smiu.datadealer.db.entity;

import javax.persistence.*;

@Entity
@Table(name = "alarm_blank", schema = "", catalog = "smiu")
public class AlarmBlank {
    private int id;
    private String condition;
    private String description;

    private TagBlank tagBlank;

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

        AlarmBlank that = (AlarmBlank) o;

        if (id != that.id) return false;
        if (condition != null ? !condition.equals(that.condition) : that.condition != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (condition != null ? condition.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AlarmBlank{" +
                "condition='" + condition + '\'' +
                '}';
    }
}
