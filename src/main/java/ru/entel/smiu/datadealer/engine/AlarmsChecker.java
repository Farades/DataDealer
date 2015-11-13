package ru.entel.smiu.datadealer.engine;

import ru.entel.smiu.datadealer.db.entity.AlarmBlank;
import ru.entel.smiu.datadealer.db.entity.Device;
import ru.entel.smiu.datadealer.protocols.service.ProtocolMaster;
import ru.entel.smiu.datadealer.protocols.service.ProtocolSlave;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;

public class AlarmsChecker extends TimerTask {
    private Map<String, ProtocolMaster> protocolMasterMap;
    private Map<Device, Set<Alarm>> activeAlarms;
    private ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("js");

    public AlarmsChecker(Engine engine) {
        this.protocolMasterMap = engine.getProtocolMasterMap();
        activeAlarms = new HashMap<>();
    }

    @Override
    public synchronized void run() {
        synchronized (activeAlarms) {
            activeAlarms.clear();
            for (ProtocolMaster protocolMaster : protocolMasterMap.values()) {
                for (ProtocolSlave slave : protocolMaster.getSlaves().values()) {
                    Set<Alarm> deviceAlarms = new HashSet<>();
                    for (AlarmBlank alarmBlank : slave.getTagBlank().getAlarmBlanks()) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(slave.getData().toString());
                        sb.append(alarmBlank.getCondition());
                        String script = sb.toString();
                        try {
                            Boolean res = (Boolean) scriptEngine.eval(script);
                            if (res == true) {
                                deviceAlarms.add(new Alarm(alarmBlank));
                            }
                        } catch (ScriptException ex) {
                            ex.printStackTrace();
                        }

                    }
                    if (deviceAlarms.size() > 0) {
                        activeAlarms.put(slave.getDevice(), deviceAlarms);
                    }
                }
            }
        }
//        System.out.println(activeAlarms);
    }

    public synchronized Map<Device, Set<Alarm>> getActiveAlarms() {
        return activeAlarms;
    }
}
