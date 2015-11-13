package ru.entel.smiu.datadealer.engine;


import ru.entel.smiu.datadealer.db.entity.AlarmBlank;

import java.util.Date;

/**
 * Created by farades on 12.11.15.
 */
public class Alarm {
    private AlarmBlank alarmBlank;
    private Date startTime;

    public Alarm(AlarmBlank alarmBlank) {
        this.alarmBlank = alarmBlank;
        this.startTime = new Date();
    }

    public Date getStartTime() {
        return startTime;
    }

    public AlarmBlank getAlarmBlank() {
        return alarmBlank;
    }

    public String getDescription() {
        return alarmBlank.getTagBlank().getTagDescr() + " " + alarmBlank.getDescription();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Alarm alarm = (Alarm) o;

        return !(alarmBlank != null ? !alarmBlank.equals(alarm.alarmBlank) : alarm.alarmBlank != null);

    }

    @Override
    public int hashCode() {
        return alarmBlank != null ? alarmBlank.hashCode() : 0;
    }
}
