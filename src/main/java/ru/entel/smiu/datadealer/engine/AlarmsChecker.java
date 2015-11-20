package ru.entel.smiu.datadealer.engine;

import ru.entel.smiu.datadealer.db.entity.DeviceEntity;
import ru.entel.smiu.datadealer.software_engine.Alarm;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.*;

public class AlarmsChecker extends TimerTask {
    private Map<DeviceEntity, Set<Alarm>> activeAlarms;
    private ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("js");

    public AlarmsChecker(Engine engine) {
        activeAlarms = new HashMap<>();
    }

    @Override
    public synchronized void run() {
        synchronized (activeAlarms) {
            activeAlarms.clear();
//            for (ProtocolMaster protocolMaster : protocolMasterMap.values()) {
//                for (ProtocolSlave slave : protocolMaster.getSlaves().values()) {
//                    if (slave.getData() == null) {
//                        return;
//                    }
//                    Set<Alarm> deviceAlarms = new HashSet<>();
//                    for (AlarmBlank alarmBlank : slave.getTagBlankEntity().getAlarmBlanks()) {
//                        if (slave.getData() instanceof ErrRegister)
//                            continue;
//                        StringBuilder sb = new StringBuilder();
//                        sb.append(slave.getData().toString());
//                        sb.append(alarmBlank.getCondition());
//                        String script = sb.toString();
//                        try {
//                            Boolean res = (Boolean) scriptEngine.eval(script);
//                            if (res == true) {
//                                deviceAlarms.add(new Alarm(alarmBlank));
//                            }
//                        } catch (ScriptException ex) {
//                            ex.printStackTrace();
//                        }
//
//                    }
//                    if (deviceAlarms.size() > 0) {
//                        activeAlarms.put(slave.getDeviceEntity(), deviceAlarms);
//                    }
//                }
//            }
        }
        System.out.println(activeAlarms);
    }

    public synchronized Map<DeviceEntity, Set<Alarm>> getActiveAlarms() {
        return activeAlarms;
    }
}
