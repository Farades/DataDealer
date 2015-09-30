package ru.entel.protocols.service;

import ru.entel.protocols.registers.AbstractRegister;

import java.util.Map;

/**
 * Created by farades on 07.05.2015.
 */
public abstract class ProtocolSlave {
    protected String name;

    public ProtocolSlave(String name, ProtocolSlaveParams params) {
        this.name = name;
        init(params);
    }

    public abstract Map<Integer, AbstractRegister> getData();

    public abstract void request() throws Exception;

    public abstract void init(ProtocolSlaveParams params);

    public String getName() {
        return name;
    }
}
