package ru.entel.protocols.modbus.rtu.master;

import ru.entel.protocols.service.ProtocolMasterParams;

/**
 * Created by Артем on 08.05.2015.
 */
public class ModbusMasterParams extends ProtocolMasterParams {
    private String portName;
    private int baudRate;
    private int dataBits;
    private String parity;
    private int stopbits;
    private String encoding;
    private boolean echo;
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
