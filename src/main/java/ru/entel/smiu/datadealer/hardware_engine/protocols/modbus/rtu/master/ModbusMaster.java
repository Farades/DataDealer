package ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.rtu.master;

import com.ghgande.j2mod.modbus.ModbusCoupler;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import org.apache.log4j.Logger;
import ru.entel.smiu.datadealer.hardware_engine.Channel;
import ru.entel.smiu.datadealer.hardware_engine.Protocol;
import ru.entel.smiu.datadealer.hardware_engine.ProtocolParams;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.exception.ModbusIllegalRegTypeException;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.exception.ModbusNoResponseException;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.exception.ModbusRequestException;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.exception.OpenComPortException;
import ru.entel.smiu.datadealer.hardware_engine.protocols.service.ProtocolType;

/**
 * Класс ModbusMaster - потомок ProtocolM.
 * Реализует интерфейс Runnable.
 * Отвечает за добавление, хранение и управление
 * объектами класса ModbusChannel в отдельном потоке.
 * @author Мацепура Артем
 * @version 0.2
 */
public class ModbusMaster extends Protocol {
    private static final Logger logger = Logger.getLogger(ModbusMaster.class);

    /**
     * Объект для коммункации с COM-портом. Передается каждому ModbusChannel при добавлении
     */
    private SerialConnection con;

    /**
     * Время задержки между вызовами метода request у объектов ModbusChannel
     */
    private int timePause;

    /**
     * Конструктор
     * @param name название данного ModbusMaster (Например: modbus_in, modbus_1)
     * @param params объект, принадлежащий классу, унаследованному от ProtocolMasterParams.
     *               Хранит в себе необходимые параметры для инициализации ModbusMaster.
     */
    public ModbusMaster(String name, ModbusMasterParams params) {
        super(name, params);
        this.type = ProtocolType.MODBUS_MASTER;
    }

    @Override
    public void init(ProtocolParams params) {
        ModbusCoupler.getReference().setUnitID(128);
        SerialParameters SerialParams = new SerialParameters();
        if (params instanceof ModbusMasterParams) {
            ModbusMasterParams mbParams = (ModbusMasterParams) params;
            SerialParams.setPortName(mbParams.getPortName());
            SerialParams.setBaudRate(mbParams.getBaudRate());
            SerialParams.setDatabits(mbParams.getDataBits());
            SerialParams.setParity(mbParams.getParity());
            SerialParams.setStopbits(mbParams.getStopbits());
            SerialParams.setEncoding(mbParams.getEncoding());
            SerialParams.setEcho(mbParams.getEcho());
            this.timePause = mbParams.getTimePause();
            con = new SerialConnection(SerialParams);
            logger.debug(this.name + " initialize.");
        } else {
            logger.error("Объект параметров не является объектом класса ModbusMasterParams");
        }
    }

    /**
     * Метод, открывающий соединение с COM-портом
     */
    public void openPort() throws OpenComPortException {
        logger.debug("\"" + this.name + "\" open Com-port connection start");
        try {
            this.con.open();
            logger.debug("\"" + this.name + "\" open Com-port connection. OK.");
        } catch (Exception ex) {
            throw new OpenComPortException("Невозможно установить соединение с Com-портом: " + ex.getMessage());
        }
    }

    /**
     * Метод, закрывающий соединение с COM-портом
     */
    public void closePort() {
        this.con.close();
        logger.debug("\"" + this.name + "\" close Com-port connection");
    }

    /**
     * Реализация интерфейса Runnable. Необходима для бесконечного цикла опроса ModbusChannel в отдельном потоке.
     */
    @Override
    public void run() {
        interviewRun = true;
        if (channels.size() > 0) {
            try {
                openPort();
                while(interviewRun) {
                    for (Channel channel : channels.values()) {
                        try {
                            channel.request();
                            Thread.sleep(timePause);
                        } catch (InterruptedException | ModbusIllegalRegTypeException | ModbusNoResponseException ex) {
                            ex.printStackTrace();
                            logger.error("\"" + channel + "\" " + ex.getMessage());
                        } catch (ModbusRequestException ex) {
                            channel.setNoResponse();
                            logger.error("\"" + channel + "\" " + ex.getMessage());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            logger.error("\"" + channel + "\" " + ex.getMessage());
                        }
                    }
                }
            } catch (OpenComPortException e) {
                e.printStackTrace();
                logger.error("\"" + this.name + "\" Невозможно установить соединение с COM-портом");
            } finally {
                closePort();
            }
        }
    }

    @Override
    public void addChannel(Channel channel) {
        ModbusChannel modbusChannel = (ModbusChannel) channel;
        modbusChannel.setCon(con);
        channels.put(modbusChannel.getName(), modbusChannel);
    }

}
