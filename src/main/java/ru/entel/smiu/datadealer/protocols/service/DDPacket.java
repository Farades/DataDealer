package ru.entel.smiu.datadealer.protocols.service;

import ru.entel.smiu.datadealer.protocols.registers.AbstractRegister;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by farades on 01.09.15.
 */
public class DDPacket implements Serializable {
    private String devID;
    private Map<Integer, AbstractRegister> registers = new HashMap<Integer, AbstractRegister>();

    public DDPacket(String devID, Map<Integer, AbstractRegister> registers) {
        this.devID = devID;
        this.registers = registers;
    }

    public String getDevID() {
        return devID;
    }

    public Map<Integer, AbstractRegister> getRegisters() {
        return registers;
    }

    @Override
    public String toString() {
        return "DDPacket{" +
                "devID='" + devID + '\'' +
                ", registers=" + registers +
                '}';
    }
}
