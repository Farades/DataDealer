package ru.entel.java.protocols.modbus.rtu.master;

import ru.entel.java.protocols.service.ProtocolMasterParams;

/**
 * ModbusMasterParams - наследник ProtocolMasterParams.
 * Хранит в себе настройки для ModbusMaster'a (в основном настройки COM-порта)
 * @author Мацепура Артем
 * @version 0.2
 */
public class ModbusMasterParams extends ProtocolMasterParams {
    /**
     * Имя COM-порта (напр. COM3, /dev/ttyUSB0)
     */
    private String portName;

    /**
     * Скорость соединения (9600, 19200)
     */
    private int baudRate;

    /**
     * Количество данных (Обычно 8)
     */
    private int dataBits;

    /**
     * Четность (Обычно none)
     */
    private String parity;

    /**
     * Количество стоповых бит (Обычно 1)
     */
    private int stopbits;

    /**
     * Типа Modbus (RTU, ASCII)
     */
    private String encoding;

    /**
     * Эхо (Обычно false)
     */
    private boolean echo;

    /**
     * Пауза между запросами в мс
     */
    private int timePause;

    public ModbusMasterParams(String portName, int baudRate, int dataBits, String parity, int stopbits, String encoding, boolean echo, int timePause) {
        this.portName = portName;
        this.baudRate = baudRate;
        this.dataBits = dataBits;
        this.parity = parity;
        this.stopbits = stopbits;
        this.encoding = encoding;
        this.echo = echo;
        this.timePause = timePause;
    }

    public String getPortName() {
        return portName;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public int getDataBits() {
        return dataBits;
    }

    public String getParity() {
        return parity;
    }

    public int getStopbits() {
        return stopbits;
    }

    public String getEncoding() {
        return encoding;
    }

    public boolean getEcho() {
        return echo;
    }

    public int getTimePause() {
        return timePause;
    }
}
