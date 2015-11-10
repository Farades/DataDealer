package ru.entel.smiu.datadealer.devices;

import ru.entel.smiu.datadealer.protocols.registers.AbstractRegister;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Created by farades on 06.11.15.
 */
public class DeviceException {
    private String varOwnerName;
    private String deviceOwner;
    private String condition;
    private String description;
    private String time_start;
    private String time_end;
    private AbstractRegister currentValue;
    private boolean active;
    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");

    public DeviceException(String varOwnerName, String deviceOwner, String condition, String description) {
        this.varOwnerName = varOwnerName;
        this.deviceOwner = deviceOwner;
        this.condition = condition;
        this.description = description;
        active = false;
    }

//    public boolean check(AbstractRegister value) {
//        this.currentValue = value;
//        String script = condition.replace(varOwnerName, value.toString());
//        try {
//            Boolean res = (Boolean)engine.eval(script);
//            if (res) {
//                this.activate();
//            } else {
//                this.desactivate();
//            }
//        } catch (ScriptException e) {
//            e.printStackTrace();
//        }
//        return this.isActive();
//    }

    public boolean isActive() {
        return active;
    }

    public String getDescription() {
        return this.description;
    }

    public String getVarOwnerName() {
        return varOwnerName;
    }

    public AbstractRegister getCurrentValue() {
        return currentValue;
    }

    public String getDeviceOwner() {
        return deviceOwner;
    }

    public String getTime_start() {
        return time_start;
    }

    public String getTime_end() {
        return time_end;
    }

//    public void activate() {
//        if (!this.active) {
//            this.active = true;
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//            this.time_start = dateFormat.format(new Date());
//            HistoryDeviceException.saveStartDeviceException(this);
//        }
//    }
//
//    public void desactivate() {
//        if (this.active) {
//            this.active = false;
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//            this.time_end = dateFormat.format(new Date());
//            HistoryDeviceException.saveEndDeviceException(this);
//        }
//
//    }

    @Override
    public String toString() {
        return "DeviceException{" +
                "condition='" + condition + '\'' +
                ", active=" + active +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceException that = (DeviceException) o;

        return condition.equals(that.condition);

    }

    @Override
    public int hashCode() {
        return condition.hashCode();
    }
}