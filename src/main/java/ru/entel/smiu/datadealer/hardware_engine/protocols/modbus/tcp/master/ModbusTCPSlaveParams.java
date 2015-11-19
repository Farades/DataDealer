package ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.tcp.master;

import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.ModbusFunction;
import ru.entel.smiu.datadealer.hardware_engine.protocols.registers.RegType;
import ru.entel.smiu.datadealer.hardware_engine.protocols.service.ProtocolSlaveParams;

/**
 * Created by farades on 18.11.15.
 */
public class ModbusTCPSlaveParams extends ProtocolSlaveParams {
    /**
     * Код функции Modbus. Хранится в enum'e ModbusFunction
     * Подробнее https://ru.wikipedia.org/wiki/Modbus
     * @see ModbusFunction
     */
    private ModbusFunction mbFunc;

    /**
     * Тип регистра Modbus
     * Подробнее https://ru.wikipedia.org/wiki/Modbus
     * @see RegType
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

    public ModbusTCPSlaveParams(ModbusFunction mbFunc, RegType mbRegType, int offset, int length) {
        this.mbFunc = mbFunc;
        this.mbRegType = mbRegType;
        this.offset = offset;
        this.length = length;
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
}
