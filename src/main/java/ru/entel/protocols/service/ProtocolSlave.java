package ru.entel.protocols.service;

import ru.entel.datadealer.db.entity.Device;
import ru.entel.datadealer.db.entity.TagBlank;
import ru.entel.protocols.registers.AbstractRegister;

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

    public abstract Map<Integer, AbstractRegister> getData();

    public Device getDevice() {
        return device;
    }

    public abstract void request() throws Exception;

    public abstract void init(ProtocolSlaveParams params);

    public String getName() {
        return name;
    }
}
