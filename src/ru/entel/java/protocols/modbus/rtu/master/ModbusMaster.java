package ru.entel.java.protocols.modbus.rtu.master;

import com.ghgande.j2mod.modbus.ModbusCoupler;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import org.apache.log4j.Logger;
import ru.entel.java.datadealer.msg.MessageService;
import ru.entel.java.datadealer.msg.MessageServiceFactory;
import ru.entel.java.datadealer.msg.MessageServiceType;
import ru.entel.java.protocols.modbus.exception.ModbusIllegalRegTypeException;
import ru.entel.java.protocols.modbus.exception.ModbusNoResponseException;
import ru.entel.java.protocols.modbus.exception.ModbusRequestException;
import ru.entel.java.protocols.modbus.exception.OpenComPortException;
import ru.entel.java.protocols.service.ProtocolMaster;
import ru.entel.java.protocols.service.ProtocolMasterParams;
import ru.entel.java.protocols.service.ProtocolSlave;
import ru.entel.java.protocols.service.ProtocolType;

import java.util.HashSet;

/**
 * Класс ModbusMaster - потомок ProtocolMaster.
 * Реализует интерфейс Runnable.
 * Отвечает за добавление, хранение и управление
 * объектами класса ModbusSlaveRead в отдельном потоке.
 * @author Мацепура Артем
 * @version 0.2
 */
public class ModbusMaster extends ProtocolMaster {
    private static final Logger logger = Logger.getLogger(ModbusMaster.class);

    /**
     * Объект MessageService предназначен для отправки данных
     */
    private MessageService messageService = MessageServiceFactory.getMessageService(MessageServiceType.MQTT);

    /**
     * Объект для коммункации с COM-портом. Передается каждому ModbusSlaveRead при добавлении
     */
    private SerialConnection con;

    /**
     * Время задержки между вызовами метода request у объектов ModbusSlaveRead
     */
    private int timePause;

    /**
     * Коллекция в которой хранятся все объекты ModbusSlaveRead для данного мастера
     */
    private HashSet<ModbusSlaveRead> slaves = new HashSet<>();

    /**
     * Флаг для остановки отдельного потока опроса объектов ModbusSlaveRead
     */
    private volatile boolean interviewRun = true;

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
    public void init(ProtocolMasterParams params) {
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
     * Метод, добавляющий ModbusSlaveRead в коллекцию данного ModbusMaster для последующего опроса.
     * @param slave Объект типа ModbusSlaveRead, суженный до родительского типа ProtocolSlave.
     */
    @Override
    public void addSlave(ProtocolSlave slave) {
        ModbusSlaveRead mbSlave = (ModbusSlaveRead) slave;
        mbSlave.setMasterName(this.name);
        mbSlave.setCon(this.con);
        slaves.add(mbSlave);
        logger.trace("Add slave: " + slave);
    }

    /**
     * Метод, открывающий соединение с COM-портом
     */
    public void openPort() throws OpenComPortException {
        try {
            this.con.open();
            logger.debug("\"" + this.name + "\" open Com-port connection");
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
     * Реализация интерфейса Runnable. Необходима для бесконечного цикла опроса ModbusSlaveRead в отдельном потоке.
     */
    @Override
    public void run() {
        interviewRun = true;
        if (slaves.size() != 0) {
            try {
                openPort();
                while(interviewRun) {
                    for (ModbusSlaveRead slave : slaves) {
                        try {
                            slave.request();
                            Thread.sleep(timePause);
                        } catch (InterruptedException | ModbusIllegalRegTypeException | ModbusNoResponseException ex) {
                            ex.printStackTrace();
                            logger.error("\"" + slave + "\" " + ex.getMessage());
                        } catch (ModbusRequestException ex) {
                            //TODO
                            messageService.send("dwa", "dwad");
                            ex.printStackTrace();
                            logger.error("\"" + slave + "\" " + ex.getMessage());
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

    /**
     * Останавливает поток опроса слейвов
     */
    @Override
    public synchronized void stopInterview() {
        this.interviewRun = false;
    }
}
