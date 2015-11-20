package ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.tcp.master;

import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import org.apache.log4j.Logger;
import ru.entel.smiu.datadealer.hardware_engine.Channel;
import ru.entel.smiu.datadealer.hardware_engine.Protocol;
import ru.entel.smiu.datadealer.hardware_engine.ProtocolParams;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.exception.ModbusRequestException;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.exception.TCPConnectException;
import ru.entel.smiu.datadealer.hardware_engine.protocols.service.ProtocolType;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ModbusTCPMaster extends Protocol {
    private static final Logger logger = Logger.getLogger(ModbusTCPMaster.class);

    /**
     * Объект для TCP коммуникации
     */
    private TCPMasterConnection con;

    private int timePause;

    private volatile boolean interviewRun = true;


    public ModbusTCPMaster(String name, ModbusTCPMasterParams params) {
        super(name, params);
        this.type = ProtocolType.MODBUS_TCP_MASTER;
    }

    @Override
    public void init(ProtocolParams params) {
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
        if (channels.size() != 0) {
            try {
                openPort();
            } catch (TCPConnectException e) {
                e.printStackTrace();
                logger.error("\"" + this.name + "\" Невозможно установить TCP соединение");
            }
            while(interviewRun) {
                    for (Channel channel : channels.values()) {
                        try {
                            channel.request();
                            Thread.sleep(timePause);
                        } catch (ModbusRequestException ex) {
                            channel.setNoResponse();
                            logger.error("\"" + channel + "\" " + ex.getMessage());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            logger.error("\"" + channel + "\" " + ex.getMessage());
                        } finally {
                            closePort();
                        }
                    }

                }
        }
    }

    @Override
    public void stopInterview() {
        this.interviewRun = false;
    }

    @Override
    public void addChannel(Channel channel) {
        ModbusTCPChannel modbusTCPChannel = (ModbusTCPChannel) channel;
        modbusTCPChannel.setCon(this.con);
        channels.put(modbusTCPChannel.getName(), modbusTCPChannel);
        logger.trace("Add slave: " + modbusTCPChannel.getName());
    }

}
