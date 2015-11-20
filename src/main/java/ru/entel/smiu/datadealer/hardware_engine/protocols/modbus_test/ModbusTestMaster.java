package ru.entel.smiu.datadealer.hardware_engine.protocols.modbus_test;

import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.rtu.master.ProtocolSlave;
import ru.entel.smiu.datadealer.hardware_engine.protocols.service.ProtocolMaster;
import ru.entel.smiu.datadealer.hardware_engine.protocols.service.ProtocolMasterParams;
import ru.entel.smiu.datadealer.hardware_engine.protocols.service.ProtocolType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by farades on 10.11.15.
 */
public class ModbusTestMaster extends ProtocolMaster {
    private Map<String, ProtocolSlave> slaves = new HashMap<>();

    private volatile boolean interviewRun = true;

    public ModbusTestMaster(String name, ProtocolMasterParams params) {
        super(name, params);
        this.type = ProtocolType.MODBUS_TEST_MASTER;
    }

    @Override
    public void run() {
        interviewRun = true;
        if (slaves.size() != 0) {
            while (interviewRun) {
                for (Map.Entry<String, ProtocolSlave> entry : slaves.entrySet()) {
                    ProtocolSlave slave = entry.getValue();
                    try {
                        slave.request();
                        Thread.sleep(50);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    @Override
    public Map<String, ProtocolSlave> getSlaves() {
        return slaves;
    }

    @Override
    public void stopInterview() {
        this.interviewRun = false;
    }

    @Override
    public void addSlave(ProtocolSlave slave) {
        ModbusTestSlave tSlave = (ModbusTestSlave) slave;
        String channelName = this.name + "." + slave.getDeviceEntity().getId() + "." + slave.getName();
        slaves.put(channelName, slave);
    }

    @Override
    public void init(ProtocolMasterParams params) {

    }
}
