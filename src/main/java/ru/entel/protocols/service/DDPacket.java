package ru.entel.protocols.service;

import ru.entel.protocols.registers.AbstractRegister;

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
}
