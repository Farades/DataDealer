package ru.entel.protocols.modbus.rtu.master;

import com.ghgande.j2mod.modbus.ModbusCoupler;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import org.apache.log4j.Logger;
import ru.entel.protocols.modbus.exception.ModbusIllegalRegTypeException;
import ru.entel.protocols.modbus.exception.ModbusNoResponseException;
import ru.entel.protocols.modbus.exception.ModbusRequestException;
import ru.entel.protocols.service.ProtocolMaster;
import ru.entel.protocols.service.ProtocolMasterParams;
import ru.entel.protocols.service.ProtocolSlave;
import ru.entel.protocols.service.ProtocolType;

import java.util.HashSet;

/**
 * Класс ModbusMaster - потомок ProtocolMaster.
 * Реализует интерфейс Runnable.
 * Отвечает за добавление, хранение и управление
 * объектами класса ModbusSlaveRead в отдельном потоке.
 * @author Мацепура Артем
 * @version 0.1
 */
public class ModbusMaster extends ProtocolMaster {
    private static final Logger logger = Logger.getLogger(ModbusMaster.class);

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
    private HashSet<ModbusSlaveRead> slaves = new HashSet<ModbusSlaveRead>();

    /**
     * Флаг для остановки отдельного потока опроса объектов ModbusSlaveRead
     */
    public volatile boolean running = true;

    /**
     * Конструктор
     * @param name название данного ModbusMaster (Например: modbus_in, modbus_1)
     * @param params объект, принадлежащий классу, унаследованному от ProtocolMasterParams.
     *               Хранит в себе необходимые параметры для инициализации ModbusMaster.
     */
    public ModbusMaster(String name, ModbusMasterParams params) {
        super(name, params);
        this.type = ProtocolType.MODBUS_MASTER;
        logger.debug(this.name + " initialize.");
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
        } else {
            //TODO добавить исключение
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
        logger.debug("Add: " + slave);
    }

    /**
     * Метод, открывающий соединение с COM-портом
     */
    public void openPort() {
        try {
            this.con.open();
            logger.info("\"" + this.name + "\" open Com-port connection");
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("\"" + this.name + "\" unable to connect to Com-port");
        }
    }

    /**
     * Метод, закрывающий соединение с COM-портом
     */
    public void closePort() {
        this.con.close();
        logger.info("\"" + this.name + "\" close Com-port connection");
    }

    /**
     * Реализация интерфейса Runnable. Необходима для бесконечного цикла опроса ModbusSlaveRead в отдельном потоке.
     */
    @Override
    public void run() {
        running = true;
        if (slaves.size() != 0) {
            openPort();
            while(running) {
                for (ModbusSlaveRead slave : slaves) {
                    try {
                        slave.request();
                        Thread.sleep(timePause);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    } catch (ModbusRequestException | ModbusIllegalRegTypeException | ModbusNoResponseException ex) {
                        logger.error("\"" + slave + "\" " + ex.getMessage());
                    }
                }
            }
            closePort();
        }
    }

    @Override
    public synchronized void stop() {
        running = false;
    }
}
