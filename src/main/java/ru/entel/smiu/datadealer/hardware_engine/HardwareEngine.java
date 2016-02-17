package ru.entel.smiu.datadealer.hardware_engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;
import ru.entel.smiu.datadealer.db.entity.ChannelEntity;
import ru.entel.smiu.datadealer.db.entity.ProtocolEntity;
import ru.entel.smiu.datadealer.db.util.DataHelper;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.ModbusFunction;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.rtu.master.ModbusChannel;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.rtu.master.ModbusChannelParams;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.rtu.master.ModbusMaster;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.rtu.master.ModbusMasterParams;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.tcp.master.ModbusTCPChannel;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.tcp.master.ModbusTCPChannelParams;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.tcp.master.ModbusTCPMaster;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.tcp.master.ModbusTCPMasterParams;
import ru.entel.smiu.datadealer.hardware_engine.protocols.registers.AbstractRegister;
import ru.entel.smiu.datadealer.hardware_engine.protocols.registers.RegType;
import ru.entel.smiu.datadealer.utils.InvalidJSONException;
import ru.entel.smiu.datadealer.utils.JSONNaturalDeserializer;
import ru.entel.smiu.datadealer.utils.JSONUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HardwareEngine - основной класс приложения. Необходим для перввоначальной конфигурации и запуска
 * опроса всех экземпляров устройств в отдельных потоках.
 * @author Мацепура Артем
 * @version 0.2
 */
public class HardwareEngine {
    private static final Logger logger = Logger.getLogger(HardwareEngine.class);

    private Map<String, Protocol> protocols = new HashMap<>();

    public Map<String, Protocol> getProtocols() {
        return protocols;
    }

    public synchronized void stop() {
        if (protocols.size() > 0) {
            for (Protocol protocol : protocols.values()) {
                protocol.stopInterview();
            }
        }
        logger.debug("Hardware Engine stoped.");
    }

    public synchronized void restart() {
        stop();
        try {
            configure();
        } catch (InvalidJSONException e) {
            e.printStackTrace();
        }
        start();
    }

    public synchronized void start() {
        for (Protocol protocol : protocols.values()) {
            new Thread(protocol, protocol.getName()).start();
        }
        logger.debug("Hardware Engine started.");

    }

    /**
     * Метод, собирающий объекты класса Protocol. Собираются из JSON'ов, хранящихся в базе данных
     * @throws InvalidJSONException
     */
    public synchronized void configure() throws InvalidJSONException {
        List<ProtocolEntity> protocolEntities = DataHelper.getInstance().getAllProtocols();
        Gson gson = new GsonBuilder().registerTypeAdapter(Object.class, new JSONNaturalDeserializer()).create();

        for (ProtocolEntity protocolEntity : protocolEntities) {
            String jsonConfig = protocolEntity.getProtocolSettings();
            if (!JSONUtils.isJSONValid(jsonConfig))
                throw new InvalidJSONException("Invalid json");

            Map protocolParams = (Map) gson.fromJson(jsonConfig, Object.class);

            switch (protocolEntity.getType()) {
                case "MODBUS_RTU_MASTER" : {
                    String masterName = protocolEntity.getName();
                    String portName = (String) protocolParams.get("portName");
                    String encoding = "rtu";
                    String parity = (String) protocolParams.get("parity");
                    int baudRate = ((Double) protocolParams.get("baudRate")).intValue();
                    int databits = ((Double) protocolParams.get("databits")).intValue();
                    int stopbits = ((Double) protocolParams.get("stopbits")).intValue();
                    int timePause = ((Double) protocolParams.get("timePause")).intValue();
                    boolean echo = false;

                    ModbusMasterParams masterParams = new ModbusMasterParams(portName, baudRate, databits, parity,
                            stopbits, encoding, echo, timePause);
                    ModbusMaster master = new ModbusMaster(masterName, masterParams);

                    for (ChannelEntity channelEntity : protocolEntity.getChannelEntities()) {
                        if (!JSONUtils.isJSONValid(channelEntity.getSettings()))
                            throw new InvalidJSONException("Invalid json");

                        Map channelParams = (Map) gson.fromJson(channelEntity.getSettings(), Object.class);

                        int unitID = ((Double) channelParams.get("unitID")).intValue();
                        ModbusFunction mbFunc = ModbusFunction.valueOf(channelParams.get("mbFunc").toString());
                        RegType regType = RegType.valueOf(channelParams.get("regType").toString());
                        int offset = ((Double) channelParams.get("offset")).intValue();
                        int length = ((Double) channelParams.get("length")).intValue();
                        int transDelay = ((Double) channelParams.get("transDelay")).intValue();

                        ModbusChannelParams sp = new ModbusChannelParams(unitID, mbFunc, regType, offset,
                                length, transDelay);
                        master.addChannel(new ModbusChannel(master.getName(), channelEntity.getName(), sp));

                    }

                    protocols.put(masterName, master);
                    break;
                }
                case "MODBUS_TCP_MASTER" : {
                    String masterName = protocolEntity.getName();
                    String ipAddress = (String) protocolParams.get("addr");
                    int port = ((Double) protocolParams.get("port")).intValue();
                    int timePause = ((Double) protocolParams.get("timePause")).intValue();

                    ModbusTCPMasterParams masterParams = new ModbusTCPMasterParams(ipAddress, port, timePause);
                    ModbusTCPMaster master = new ModbusTCPMaster(masterName, masterParams);

                    for (ChannelEntity channelEntity : protocolEntity.getChannelEntities()) {
                        if (!JSONUtils.isJSONValid(channelEntity.getSettings()))
                            throw new InvalidJSONException("Invalid json");

                        Map channelParams = (Map) gson.fromJson(channelEntity.getSettings(), Object.class);
                        ModbusFunction mbFunc = ModbusFunction.valueOf(channelParams.get("mbFunc").toString());
                        RegType regType = RegType.valueOf(channelParams.get("regType").toString());
                        int offset = ((Double) channelParams.get("offset")).intValue();
                        int length = ((Double) channelParams.get("length")).intValue();

                        ModbusTCPChannelParams modbusTCPChannelParams = new ModbusTCPChannelParams(mbFunc, regType, offset, length, ipAddress);
                        master.addChannel(new ModbusTCPChannel(master.getName(), channelEntity.getName(), modbusTCPChannelParams));
                    }
                    protocols.put(masterName, master);

                    break;
                }
            }
        }

        logger.debug("Hardware Engine configure");
    }

    /**
     * Метод, возвращающий конкретный регистер по ID.
     * @param id ID конкретного регистра
     * @return Ссылка типа AbstractRegister, указывающий на объект конкретного класса-потомка
     */
    public synchronized AbstractRegister getRegisterByID(String id) {
        String[] splits = id.split("\\.");
        Protocol protocol = protocols.get(splits[0]);
        Channel channel = protocol.getChannels().get(splits[0] + "." + splits[1]);
        AbstractRegister register = channel.getAbstractRegisterByNumber(Integer.valueOf(splits[2]));
        return register;
    }
}
