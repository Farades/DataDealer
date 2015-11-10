package ru.entel.smiu.datadealer.db.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by farades on 06.11.15.
 */
@Entity
@Table(name = "values", schema = "", catalog = "smiu")
public class Values {
    private int id;
    private String name;
    private String value;
    private String device;
    private Timestamp time;

    public Values(String name, String value, String device) {
        this.name = name;
        this.value = value;
        this.device = device;
        Date date = new Date();
        time = new Timestamp(date.getTime());
    }

    public Values() {

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
    @Column(name = "name", nullable = false, insertable = true, updatable = true, length = 45)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    @Column(name = "device", nullable = false, insertable = true, updatable = true, length = 45)
    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    @Basic
    @Column(name = "time", nullable = true, insertable = true, updatable = true)
    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Values values = (Values) o;

        if (id != values.id) return false;
        if (name != null ? !name.equals(values.name) : values.name != null) return false;
        if (value != null ? !value.equals(values.value) : values.value != null) return false;
        if (device != null ? !device.equals(values.device) : values.device != null) return false;
        if (time != null ? !time.equals(values.time) : values.time != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (device != null ? device.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        return result;
    }
}
