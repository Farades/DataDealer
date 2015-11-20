package ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.tcp.master;

import ru.entel.smiu.datadealer.hardware_engine.ChannelParams;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.ModbusFunction;
import ru.entel.smiu.datadealer.hardware_engine.protocols.registers.RegType;

public class ModbusTCPChannelParams extends ChannelParams {
    /**
     * Код функции Modbus. Хранится в enum'e ModbusFunction
     * Подробнее https://ru.wikipedia.org/wiki/Modbus
     * @see ModbusFunction
     */
    private ModbusFunction mbFunc;

    private String addres;

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

    public ModbusTCPChannelParams(ModbusFunction mbFunc, RegType mbRegType, int offset, int length, String addres) {
        this.mbFunc = mbFunc;
        this.mbRegType = mbRegType;
        this.offset = offset;
        this.length = length;
        this.addres = addres;
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

    public String getAddres() {
        return addres;
    }
}
