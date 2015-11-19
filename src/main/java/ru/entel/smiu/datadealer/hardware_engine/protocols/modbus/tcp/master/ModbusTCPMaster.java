package ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.tcp.master;

import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import org.apache.log4j.Logger;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.exception.TCPConnectException;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.rtu.master.ProtocolSlave;
import ru.entel.smiu.datadealer.hardware_engine.protocols.service.ProtocolMaster;
import ru.entel.smiu.datadealer.hardware_engine.protocols.service.ProtocolMasterParams;
import ru.entel.smiu.datadealer.hardware_engine.protocols.service.ProtocolType;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class ModbusTCPMaster extends ProtocolMaster {
    private static final Logger logger = Logger.getLogger(ModbusTCPMaster.class);

    /**
     * Объект для TCP коммуникации
     */
    private TCPMasterConnection con;

    private Map<String, ProtocolSlave> slaves = new HashMap<>();

    private int timePause;

    private volatile boolean interviewRun = true;


    public ModbusTCPMaster(String name, ModbusTCPMasterParams params) {
        super(name, params);
        this.type = ProtocolType.MODBUS_TCP_MASTER;
    }

    @Override
    public void init(ProtocolMasterParams params) {
        if (params instanceof ModbusTCPMasterParams) {
            ModbusTCPMasterParams masterParams = (ModbusTCPMasterParams) params;
            try {
                InetAddress address = InetAddress.getByName(masterParams.getIpAddress());
                con = new TCPMasterConnection(address);
                con.setPort(masterParams.getPort());
                this.timePause = masterParams.getTimePause();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    private void openPort() throws TCPConnectException {
        try {
            this.con.connect();
        } catch (Exception e) {
            throw new TCPConnectException("Невозможно установить соединение с TCP: " + e.getMessage());
        }
    }

    private void closePort() {
        this.con.close();
    }

    @Override
    public void run() {
        interviewRun = true;
        if (slaves.size() != 0) {
            try {
                openPort();
            } catch (TCPConnectException e) {
                e.printStackTrace();
                logger.error("\"" + this.name + "\" Невозможно установить TCP соединение");
            }
            while(interviewRun) {

                    for (Map.Entry<String, ProtocolSlave> entry : slaves.entrySet()) {
                        ProtocolSlave slave = entry.getValue();
                        try {
                            slave.request();
//                            Thread.sleep(timePause);
//                        } catch (ModbusRequestException ex) {
//                            //TODO
//                            slave.setNoResponse();
//                            logger.error("\"" + slave + "\" " + ex.getMessage());
////                            String topic = "smiu/DD" + this.name + ":" + slave.getName() + "/data";
////                            messageService.send(topic, "SlaveErr");
////                            ex.printStackTrace();
////                            logger.error("\"" + slave + "\" " + ex.getMessage());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            logger.error("\"" + slave + "\" " + ex.getMessage());
                        } finally {
                            closePort();
                        }
                    }

                }
        }
    }

    @Override
    public Map<String, ProtocolSlave> getSlaves() {
        return this.slaves;
    }

    @Override
    public void stopInterview() {
        this.interviewRun = false;
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
