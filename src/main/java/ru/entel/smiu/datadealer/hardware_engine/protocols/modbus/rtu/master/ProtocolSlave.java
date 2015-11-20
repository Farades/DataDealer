package ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.rtu.master;

import ru.entel.smiu.datadealer.db.entity.DeviceEntity;
import ru.entel.smiu.datadealer.db.entity.TagBlankEntity;
import ru.entel.smiu.datadealer.hardware_engine.protocols.registers.AbstractRegister;
import ru.entel.smiu.datadealer.hardware_engine.protocols.service.ProtocolSlaveParams;

/**
 * Created by farades on 07.05.2015.
 */
public abstract class ProtocolSlave {
    protected String name;
    protected DeviceEntity deviceEntity;
    protected TagBlankEntity tagBlankEntity;

    public ProtocolSlave(String name, ProtocolSlaveParams params, DeviceEntity deviceEntity, TagBlankEntity tagBlankEntity) {
        this.name = name;
        this.deviceEntity = deviceEntity;
        this.tagBlankEntity = tagBlankEntity;
        init(params);
    }

    public abstract AbstractRegister getData();

    public abstract void setNoResponse();

    public DeviceEntity getDeviceEntity() {
        return deviceEntity;
    }

    public TagBlankEntity getTagBlankEntity() {
        return tagBlankEntity;
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