package ru.entel.java.protocols.modbus.exception;

/**
 * OpenComPortException - исключение, выкидываемое при невозможности открыть COM-порт
 */
public class OpenComPortException extends Exception {
    public OpenComPortException(String message) {
        super(message);
    }
}
