package ru.entel.protocols.modbus.exception;

/**
 * Created by farades on 07.05.2015.
 */
public class ModbusNoResponseException extends Exception {
    public ModbusNoResponseException(String msg) {
        super(msg);
    }
}

