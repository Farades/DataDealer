package ru.entel.smiu.datadealer.protocols.modbus.exception;

/**
 * ModbusIllegalRegTypeException - исключение, выкидываемое при неправильном использовании типов регистров
 */
public class ModbusIllegalRegTypeException extends Exception {
    public ModbusIllegalRegTypeException(String msg) {
        super(msg);
    }
}