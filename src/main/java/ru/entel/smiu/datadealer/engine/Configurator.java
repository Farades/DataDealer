package ru.entel.smiu.datadealer.engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import ru.entel.smiu.datadealer.db.entity.DeviceEntity;
import ru.entel.smiu.datadealer.db.entity.ProtocolEntity;
import ru.entel.smiu.datadealer.db.util.DataHelper;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.tcp.master.ModbusTCPMaster;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.tcp.master.ModbusTCPMasterParams;
import ru.entel.smiu.msg.ConfigData;
import ru.entel.smiu.msg.DeviceConfPackage;
import ru.entel.smiu.msg.MqttService;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.rtu.master.ModbusMaster;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.rtu.master.ModbusMasterParams;
import ru.entel.smiu.datadealer.hardware_engine.protocols.service.InvalidProtocolTypeException;
import ru.entel.smiu.datadealer.hardware_engine.protocols.service.ProtocolMaster;
import ru.entel.smiu.datadealer.utils.InvalidJSONException;
import ru.entel.smiu.datadealer.utils.JSONNaturalDeserializer;
import ru.entel.smiu.datadealer.utils.JSONUtils;

import java.util.*;

/**
 * Configurator - класс-конфигуратор. необходим для инициализации всего приложения
 * @author Мацепура Артем
 * @version 0.2
 */
public class Configurator implements MqttCallback {
    private static final Logger logger = Logger.getLogger(Configurator.class);
    public static List<ProtocolEntity> protocolEntities;

    private Engine engine;

    /**
     * Основная переменная для работы с MQTT
     */
    private MqttClient client;

    /**
     * Ветка, которую слушает MQTT client для обновления конфигов
     */
    private final String CONFIG_TOPIC = "smiu/DD/config/update";

    /**
     * Ветка, которую слушает MQTT client для обновления конфигов
     */
    private final String GET_CONFIG_TOPIC = "smiu/DD/config/get";

    /**
     * Ветка, которую слушает MQTT client для обновления конфигов
     */
    private final String OUT_CONFIG_TOPIC = "smiu/DD/config/out";


    private Gson gson;
    /**
     * Строковый идентификатор данного клиента
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final String CLIENT_ID = "DD-config";

    public Configurator(Engine engine) {
        this.engine = engine;
        mqttInit();
        GsonBuilder builder = new GsonBuilder();
        gson = builder.create();
    }

    /**
     * Основной метод класса. Использует синглтон DataHelper для получения настроек проекта.
     * @return Словарь(HashMap): ключ - название мастера протокола; значение - объект ProtocolMaster, готовый к запуску.
     * @throws InvalidJSONException Невалидный JSON
     * @throws InvalidProtocolTypeException Неизвестный типа протокола
     */
    public synchronized Map<String, ProtocolMaster> getProtocolMasters() throws InvalidJSONException, InvalidProtocolTypeException {
        Map<String, ProtocolMaster> res = new HashMap<>();

        protocolEntities = DataHelper.getInstance().getAllProtocols();
        Gson gson = new GsonBuilder().registerTypeAdapter(Object.class, new JSONNaturalDeserializer()).create();

        for (ProtocolEntity protocolEntity : protocolEntities) {
            String jsonConfig = protocolEntity.getProtocolSettings();
            if (!JSONUtils.isJSONValid(jsonConfig))
                throw new InvalidJSONException("Invalid json");

            Map protocolParams = (Map) gson.fromJson(jsonConfig, Object.class);

            switch (protocolEntity.getType()) {
                case "MODBUS_RTU_MASTER": {
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

//                    for (DeviceEntity deviceEntity : protocolEntity.getDeviceEntities()) {
//                        String jsonDevConf = deviceEntity.getDeviceSettings();
//                        if (!JSONUtils.isJSONValid(jsonDevConf))
//                            throw new InvalidJSONException("Invalid json");
//
//                        Map slaveParams = (Map) gson.fromJson(jsonDevConf, Object.class);
//
//                        int unitID = ((Double) slaveParams.get("unitId")).intValue();
//                        DeviceBlank deviceBlank = deviceEntity.getDeviceBlank();
//                        for (TagBlankEntity tagBlankEntity : deviceBlank.getTagBlankEntities()) {
//                            String tagParams[] = tagBlankEntity.getTagId().split(":");
//                            ModbusFunction mbFunc = ModbusFunction.valueOf(String.valueOf(tagParams[0]));
//                            RegType regType = RegType.valueOf(String.valueOf(tagParams[1]));
//                            int offset = Integer.valueOf(tagParams[2]);
//                            int length = 1;
//                            int transDelay = tagBlankEntity.getDelay();
//                            String slaveName = tagBlankEntity.getTagName();
//
//                            ModbusChannelParams sp = new ModbusChannelParams(unitID, mbFunc, regType, offset,
//                                    length, transDelay);
////                            master.addSlave(new ModbusChannel(slaveName, sp, deviceEntity, tagBlankEntity));
//                        }
//
//                    }
//                    res.put(masterName, master);
                    break;
                }
                case "MODBUS_TEST": {
//                    String protocolName = protocolEntity.getName();
//                    ProtocolMasterParams masterParams = null;
//                    ModbusTestMaster testMaster = new ModbusTestMaster(protocolName, masterParams);
//                    for (DeviceEntity deviceEntity : protocolEntity.getDeviceEntities()) {
//                        DeviceBlank deviceBlank = deviceEntity.getDeviceBlank();
//                        for (TagBlankEntity tagBlankEntity : deviceBlank.getTagBlankEntities()) {
//                            String slaveName = tagBlankEntity.getTagName();
//                            ModbusTestSlaveParams sp = new ModbusTestSlaveParams();
//                            testMaster.addSlave(new ModbusTestSlave(slaveName, sp, deviceEntity, tagBlankEntity));
//                        }
//                    }
                    System.out.println();
//                    res.put(protocolName, testMaster);
                    break;
                }
                case "MODBUS_TCP_MASTER": {
                    String masterName = protocolEntity.getName();
                    String ipAddress = (String) protocolParams.get("addr");
                    int port = ((Double) protocolParams.get("port")).intValue();
                    int timePause = ((Double) protocolParams.get("timePause")).intValue();

                    ModbusTCPMasterParams masterParams = new ModbusTCPMasterParams(ipAddress, port, timePause);
                    ModbusTCPMaster master = new ModbusTCPMaster(masterName, masterParams);

//                    for (DeviceEntity deviceEntity : protocolEntity.getDeviceEntities()) {
//                        String jsonDevConf = deviceEntity.getDeviceSettings();
//                        if (!JSONUtils.isJSONValid(jsonDevConf))
//                            throw new InvalidJSONException("Invalid json");
//
//                        Map slaveParams = (Map) gson.fromJson(jsonDevConf, Object.class);
//
//                        DeviceBlank deviceBlank = deviceEntity.getDeviceBlank();
//                        for (TagBlankEntity tagBlankEntity : deviceBlank.getTagBlankEntities()) {
//                            String tagParams[] = tagBlankEntity.getTagId().split(":");
//                            ModbusFunction mbFunc = ModbusFunction.valueOf(String.valueOf(tagParams[0]));
//                            RegType regType = RegType.valueOf(String.valueOf(tagParams[1]));
//                            int offset = Integer.valueOf(tagParams[2]);
//                            int length = 1;
//                            String slaveName = tagBlankEntity.getTagName();
//
//                            ModbusTCPChannelParams sp = new ModbusTCPChannelParams(mbFunc, regType, offset,
//                                    length);
//                            master.addSlave(new ModbusTCPChannel(slaveName, sp, deviceEntity, tagBlankEntity));
//                        }
//
//                    }

//                    res.put(masterName, master);

                    break;
                }
                default:
                    throw new InvalidProtocolTypeException("Invalid protocolEntity type - " + protocolEntity.getType());
            }
        }
        return res;
    }

    /**
     * Инициализация MQTT клиента. Подпись на ветку CONFIG_TOPIC
     */
    private void mqttInit() {
        try {
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);
            client = new MqttClient(MqttService.BROKER_URL, CLIENT_ID, new MemoryPersistence());

            client.setCallback(this);
            client.connect(connectOptions);
            client.subscribe(CONFIG_TOPIC, MqttService.QOS);
        } catch (MqttException e) {
            e.printStackTrace();
            logger.error("Ошибка в функции mqttInit(): " + e.getMessage());
        }
    }


    public void updateConfig() throws InvalidJSONException {
        logger.debug("Configurator update config");
     }

    public void sendConfig() {
        List<DeviceEntity> deviceEntities = DataHelper.getInstance().getAllDevices();
        Map<String, String> resProperties = new HashMap<>();
        Set<DeviceConfPackage> resDevices = new HashSet<>();
        for (DeviceEntity deviceEntity : deviceEntities) {
            DeviceConfPackage dcp = new DeviceConfPackage(deviceEntity.getName(), deviceEntity.getDeviceBlank().getDeviceType());
            resDevices.add(dcp);
        }
        ConfigData configData = new ConfigData(resProperties, resDevices);
        String outJson = gson.toJson(configData);
        MqttService.getInstance().send(OUT_CONFIG_TOPIC, outJson);
    }

    /**
     * Перегруженный CallBack-метод интерфейса MqttCallback
     * Вызывается при потери соединения с MQTT сервером
     * @param throwable исключение
     */
    @Override
    public void connectionLost(Throwable throwable) {

    }

    /**
     * Перегруженный CallBack-метод интерфейса MqttCallback
     * Вызывается при получении сообщения в подисанной ветке
     * @param s Название топика
     * @param mqttMessage Сообщение
     * @throws Exception
     */
    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        if (s.equals(CONFIG_TOPIC)) {
            updateConfig();
        } else if (s.equals(GET_CONFIG_TOPIC)) {
            sendConfig();
        }
    }

    /**
     * Перегруженный CallBack-метод интерфейса MqttCallback
     * Вызывается при доставке сообщение на сервер.
     * Используется для асинхронного обмена информацией с сервером.
     * Синхронная доставка используется с помощью
     * MqttDeliveryToken token = topic.publish(message);
     * token.waitForCompletion();
     * @param iMqttDeliveryToken Токен доставки
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        client.disconnect();
    }
}
