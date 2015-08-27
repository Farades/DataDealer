package ru.entel.protocols.modbus.rtu.master;

import ru.entel.protocols.modbus.ModbusFunction;
import ru.entel.protocols.registers.RegType;
import ru.entel.protocols.service.ProtocolSlaveParams;

/**
 * Created by Артем on 08.05.2015.
 */
public class ModbusSlaveParams extends ProtocolSlaveParams {
    private int unitId;
    private ModbusFunction mbFunc;
    private RegType mbRegType;
    private int offset;
    private int length;
    private int transDelay;

    public ModbusSlaveParams(int unitId, ModbusFunction mbFunc, RegType mbRegType, int offset, int length, int transDelay) {
        this.unitId = unitId;
        this.mbFunc = mbFunc;
        this.mbRegType = mbRegType;
        this.offset = offset;
        this.length = length;
        this.transDelay = transDelay;
    }

    public int getUnitId() {
        return unitId;
    }

    public ModbusFunction getMbFunc() {
        return mbFunc;
    }

    public RegType getMbRegType() {
        return mbRegType;
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }

    public int getTransDelay() {
        return transDelay;
    }
}
