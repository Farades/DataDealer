package ru.entel.java.protocols.modbus.rtu.master;

import ru.entel.java.protocols.modbus.ModbusFunction;
import ru.entel.java.protocols.registers.RegType;
import ru.entel.java.protocols.service.ProtocolSlaveParams;

/**
 * ModbusSlaveParams - наследник ProtocolSlaveParams.
 * Хранит в себе настройки, необходимые для опроса конкретного слейва.
 * @author Мацепура Артем
 * @version 0.2
 */
public class ModbusSlaveParams extends ProtocolSlaveParams {
    /**
     * ID устройства в Modbus сети
     */
    private int unitId;

    /**
     * Код функции Modbus. Хранится в enum'e ModbusFunction
     * Подробнее https://ru.wikipedia.org/wiki/Modbus
     * @see ru.entel.java.protocols.modbus.ModbusFunction
     */
    private ModbusFunction mbFunc;

    /**
     * Тип регистра Modbus
     * Подробнее https://ru.wikipedia.org/wiki/Modbus
     * @see ru.entel.java.protocols.registers.RegType
     */
    private RegType mbRegType;

    /**
     * Адрес первого регистра для чтения
     */
    private int offset;

    /**
     * Количество считываемых регистров
     */
    private int length;

    /**
     * Время ожидания ответа
     */
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
