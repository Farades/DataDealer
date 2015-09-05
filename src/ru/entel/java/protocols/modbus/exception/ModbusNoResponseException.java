package ru.entel.java.protocols.modbus.exception;

/**
 * ModbusNoResponseException - Исключение, выкидываемое при отсутствии ответа от слейва
 */
public class ModbusNoResponseException extends Exception {
    public ModbusNoResponseException(String msg) {
        super(msg);
    }
}

