package ru.entel.smiu.datadealer.protocols.modbus.tcp.master;

import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import org.apache.log4j.Logger;
import ru.entel.smiu.datadealer.protocols.modbus.exception.TCPConnectException;
import ru.entel.smiu.datadealer.protocols.service.ProtocolMaster;
import ru.entel.smiu.datadealer.protocols.service.ProtocolMasterParams;
import ru.entel.smiu.datadealer.protocols.service.ProtocolSlave;
import ru.entel.smiu.datadealer.protocols.service.ProtocolType;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class ModbusTCPMaster extends ProtocolMaster {
    private static final Logger logger = Logger.getLogger(ModbusTCPMaster.class);

    /**
     * Объект для TCP коммуникации
     */
    private TCPMasterConnection con;

    private Map<String, ProtocolSlave> slaves = new HashMap<>();

    private volatile boolean interviewRun = true;


    public ModbusTCPMaster(String name, ModbusTCPMasterParams params) {
        super(name, params);
        this.type = ProtocolType.MODBUS_TCP_MASTER;
    }

    @Override
    public void init(ProtocolMasterParams params) {
//        addr = InetAddress.getByName("192.168.10.189");
//        con = new TCPMasterConnection(addr);
//        con.setPort(502);
//        con.connect();
    }

    private void openPort() throws TCPConnectException {
        try {
            this.con.connect();
        } catch (Exception e) {
            throw new TCPConnectException("Невозможно установить соединение с TCP: " + e.getMessage());
        }
    }

    @Override
    public void run() {

    }

    @Override
    public Map<String, ProtocolSlave> getSlaves() {
        return null;
    }

    @Override
    public void stopInterview() {

    }

    @Override
    public void addSlave(ProtocolSlave slave) {
        ModbusTCPSlave modbusTCPSlave = (ModbusTCPSlave) slave;
        modbusTCPSlave.setProtocolName(this.name);
        modbusTCPSlave.setCon(this.con);
        String channelName = this.name + "." + slave.getDevice().getId() + "." + slave.getName();
        slaves.put(channelName, slave);
        logger.trace("Add slave: " + slave);
    }

}
