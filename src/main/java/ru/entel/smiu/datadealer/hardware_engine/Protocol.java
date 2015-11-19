package ru.entel.smiu.datadealer.hardware_engine;

import ru.entel.smiu.datadealer.hardware_engine.protocols.registers.AbstractRegister;
import ru.entel.smiu.datadealer.hardware_engine.protocols.service.ProtocolType;

import java.util.HashMap;
import java.util.Map;

public abstract class Protocol implements Runnable {
    protected String name;

    protected ProtocolType type;

    protected Map<String, Channel> channels = new HashMap<>();

    public Protocol(String name, ProtocolParams params) {
        this.name = name;
        init(params);
    }

    protected abstract void init(ProtocolParams params);

    public abstract void stopInterview();

    public abstract void addChannel(Channel channel);

    public String getName() {
        return name;
    }

    public Map<String, Channel> getChannels() {
        return channels;
    }

    public synchronized AbstractRegister getRegisterByName(String name) {
//        modbus_in.device1_1.1
//        String
//        return channels.get;
        return null;
    }

    @Override
    public String toString() {
        return "ProtocolEntity{" +
                "channels=" + channels +
                '}';
    }
}
