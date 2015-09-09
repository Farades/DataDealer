package ru.entel.java.protocols.service;

/**
 * Created by farades on 07.05.2015.
 */
public abstract class ProtocolSlave {
    protected String name;

    public ProtocolSlave(String name, ProtocolSlaveParams params) {
        this.name = name;
        init(params);
    }

    public abstract void request() throws Exception;

    public abstract void init(ProtocolSlaveParams params);

    public String getName() {
        return name;
    }
}
