package ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.exception;

/**
 * Created by farades on 18.11.15.
 */
public class TCPConnectException extends Exception {
    public TCPConnectException(String msg) {
        super(msg);
    }
}
