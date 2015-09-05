package ru.entel.java.protocols.service;

import ru.entel.java.protocols.registers.AbstractRegister;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by farades on 01.09.15.
 */
public class DDPacket {
    private boolean readErr;
    private String masterName;
    private String slaveName;
    private Map<Integer, AbstractRegister> registers = new HashMap<Integer, AbstractRegister>();

    public DDPacket(boolean readErr, String masterName, String slaveName, Map<Integer, AbstractRegister> registers) {
        this.readErr = readErr;
        this.masterName = masterName;
        this.slaveName = slaveName;
        this.registers = registers;
    }

    public DDPacket(boolean readErr) {
        this.readErr = readErr;
    }
}
