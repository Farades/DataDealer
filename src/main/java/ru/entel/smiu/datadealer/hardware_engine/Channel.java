package ru.entel.smiu.datadealer.hardware_engine;

import ru.entel.smiu.datadealer.hardware_engine.protocols.registers.AbstractRegister;

import java.util.HashMap;
import java.util.Map;

public abstract class Channel {
    protected String name;

    /**
     * Коллекция в которой хранятся последние значения обработанных регистров
     */
    protected Map<Integer, AbstractRegister> registers = new HashMap<Integer, AbstractRegister>();

    public Channel(String protocolName, String name, ChannelParams params) {
        this.name = protocolName + "." + name;
        init(params);
    }

    public abstract void init(ChannelParams params);

    public synchronized void setNoResponse() {
        for (AbstractRegister register : registers.values()) {
            register.setValid(false);
        }
    }

    public abstract void request() throws Exception;

    public synchronized AbstractRegister getAbstractRegisterByNumber(Integer number) {
        return registers.get(number);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ChannelEntity{" +
                "name='" + name + '\'' +
                ", registers=" + registers +
                '}';
    }
}
