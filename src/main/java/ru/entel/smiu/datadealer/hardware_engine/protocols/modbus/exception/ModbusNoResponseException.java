package ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.exception;

/**
 * ModbusNoResponseException - Исключение, выкидываемое при отсутствии ответа от слейва
 */
public class ModbusNoResponseException extends Exception {
    public ModbusNoResponseException(String msg) {
        super(msg);
    }
}

