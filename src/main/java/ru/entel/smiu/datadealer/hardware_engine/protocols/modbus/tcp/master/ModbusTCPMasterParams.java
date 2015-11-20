package ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.tcp.master;

import ru.entel.smiu.datadealer.hardware_engine.ProtocolParams;

public class ModbusTCPMasterParams extends ProtocolParams {
    private String ipAddress;

    private int port;

    /**
     * Пауза между запросами в мс
     */
    private int timePause;

    public ModbusTCPMasterParams(String ipAddress, int port, int timePause) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.timePause = timePause;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public int getTimePause() {
        return timePause;
    }
}
