package ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.rtu.master;

import ru.entel.smiu.datadealer.db.entity.Device;
import ru.entel.smiu.datadealer.db.entity.TagBlank;
import ru.entel.smiu.datadealer.hardware_engine.protocols.registers.AbstractRegister;
import ru.entel.smiu.datadealer.hardware_engine.protocols.service.ProtocolSlaveParams;

import java.util.Map;

/**
 * Created by farades on 07.05.2015.
 */
public abstract class ProtocolSlave {
    protected String name;
    protected Device device;
    protected TagBlank tagBlank;

    public ProtocolSlave(String name, ProtocolSlaveParams params, Device device, TagBlank tagBlank) {
        this.name = name;
        this.device = device;
        this.tagBlank = tagBlank;
        init(params);
    }

    public abstract AbstractRegister getData();

    public abstract void setNoResponse();

    public Device getDevice() {
        return device;
    }

    public TagBlank getTagBlank() {
        return tagBlank;
    }

    public abstract void request() throws Exception;

    public abstract void init(ProtocolSlaveParams params);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ProtocolSlave{" +
                "name='" + name + '\'' +
                '}';
    }
}