package ru.entel.smiu.datadealer.protocols.modbus.tcp.master;

import ru.entel.smiu.datadealer.protocols.service.ProtocolMasterParams;

public class ModbusTCPMasterParams extends ProtocolMasterParams {
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
