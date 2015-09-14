package ru.entel.protocols.service;

import ru.entel.protocols.registers.AbstractRegister;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by farades on 01.09.15.
 */
public class DDPacket implements Serializable {
    private String masterName;
    private String slaveName;
    private Map<Integer, AbstractRegister> registers = new HashMap<Integer, AbstractRegister>();

    public DDPacket(String masterName, String slaveName, Map<Integer, AbstractRegister> registers) {
        this.masterName = masterName;
        this.slaveName = slaveName;
        this.registers = registers;
    }
}
