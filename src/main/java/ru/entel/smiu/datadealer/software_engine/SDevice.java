package ru.entel.smiu.datadealer.software_engine;

import ru.entel.smiu.datadealer.db.entity.AlarmBlank;
import ru.entel.smiu.datadealer.db.entity.AlarmEntity;
import ru.entel.smiu.datadealer.db.entity.DeviceEntity;
import ru.entel.smiu.datadealer.db.entity.TagBlankEntity;
import ru.entel.smiu.datadealer.db.util.DataHelper;
import ru.entel.smiu.datadealer.engine.Engine;

import javax.script.ScriptException;
import java.util.*;

public class SDevice {

    private transient DeviceEntity deviceEntity;
    private Map<String, Value> values = new HashMap<>();
    private Set<Alarm> activeAlarms = new HashSet<>();

    public SDevice(DeviceEntity deviceEntity) {
        this.deviceEntity = deviceEntity;
        init();
    }

    private synchronized void init() {
        updateValues();
    }

    public synchronized void update() {
        updateValues();
        updateAlarms();
    }

    private synchronized void updateAlarms() {
//        activeAlarms.clear();
        for (TagBlankEntity tagBlankEntity : deviceEntity.getTagBlankEntities()) {
            for (AlarmBlank alarmBlank : tagBlankEntity.getAlarmBlanks()) {
                StringBuffer sb = new StringBuffer();

                try {
                    sb.append(values.get(tagBlankEntity.getTagDescr()).getRegister());
                    sb.append(alarmBlank.getCondition());
                    String script = sb.toString();
                    Boolean res = (Boolean) AlarmUtil.getInstance().getScriptEngine().eval(script);
                    Alarm alarm = new Alarm(alarmBlank);
                    if (res == true) {
                        if (activeAlarms.contains(alarm)) {
                            continue;
                        } else {
                            activeAlarms.add(alarm);
                            AlarmEntity alarmEntity = new AlarmEntity();
                            alarmEntity.setAlarmBlank(alarmBlank);
                            alarmEntity.setAlarmTime(alarm.getStartTime());
                            DataHelper.getInstance().saveAlarm(alarmEntity);
                        }
                    } else {
                        if (activeAlarms.contains(alarm)) {
                            activeAlarms.remove(alarm);
                        }
                    }
                } catch (RuntimeException ex) {
                    ex.printStackTrace();
                } catch (ScriptException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Map<String, Value> getValues() {
        return values;
    }

    public DeviceEntity getDeviceEntity() {
        return deviceEntity;
    }

    public Set<Alarm> getActiveAlarms() {
        return activeAlarms;
    }

    private synchronized void updateValues() {
        for (TagBlankEntity tagBlankEntity : deviceEntity.getTagBlankEntities()) {
            Value value = new Value(Engine.getInstance().getHardwareEngine().getRegisterByID(tagBlankEntity.getTagBinding())
                    , tagBlankEntity);
            values.put(tagBlankEntity.getTagDescr(), value);
        }
    }

    @Override
    public String toString() {
        return "SDevice{" +
                "values=" + values +
                '}';
    }
}
