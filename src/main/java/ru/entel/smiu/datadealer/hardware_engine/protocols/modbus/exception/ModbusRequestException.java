package ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.exception;

/**
 * ModbusRequestException - исключение, выкидываемое при сбое запроса.
 * Например: неверный адрес, illegal data type, failed to read, ...
 */
public class ModbusRequestException extends Exception {
    public ModbusRequestException(String msg) {
        super(msg);
    }
}
