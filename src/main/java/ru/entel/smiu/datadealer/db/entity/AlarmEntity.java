package ru.entel.smiu.datadealer.db.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "alarm", schema = "", catalog = "smiu")
public class AlarmEntity {
    private int id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date alarmTime;
    private AlarmBlank alarmBlank;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "alarm_time")
    public Date getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(Date alarmTime) {
        this.alarmTime = alarmTime;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "alarm_blank_id")
    public AlarmBlank getAlarmBlank() {
        return alarmBlank;
    }

    public void setAlarmBlank(AlarmBlank alarmBlank) {
        this.alarmBlank = alarmBlank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AlarmEntity that = (AlarmEntity) o;

        if (id != that.id) return false;
        if (alarmTime != null ? !alarmTime.equals(that.alarmTime) : that.alarmTime != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (alarmTime != null ? alarmTime.hashCode() : 0);
        return result;
    }
}
