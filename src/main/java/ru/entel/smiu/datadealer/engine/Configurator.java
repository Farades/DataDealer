package ru.entel.smiu.datadealer.engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import ru.entel.smiu.datadealer.db.entity.Device;
import ru.entel.smiu.datadealer.db.entity.DeviceBlank;
import ru.entel.smiu.datadealer.db.entity.Protocol;
import ru.entel.smiu.datadealer.db.entity.TagBlank;
import ru.entel.smiu.datadealer.db.util.DataHelper;
import ru.entel.smiu.msg.MqttService;
import ru.entel.smiu.datadealer.protocols.modbus.ModbusFunction;
import ru.entel.smiu.datadealer.protocols.modbus.rtu.master.ModbusMaster;
import ru.entel.smiu.datadealer.protocols.modbus.rtu.master.ModbusMasterParams;
import ru.entel.smiu.datadealer.protocols.modbus.rtu.master.ModbusSlaveParams;
import ru.entel.smiu.datadealer.protocols.modbus.rtu.master.ModbusSlaveRead;
import ru.entel.smiu.datadealer.protocols.modbus_test.ModbusTestMaster;
import ru.entel.smiu.datadealer.protocols.modbus_test.ModbusTestSlave;
import ru.entel.smiu.datadealer.protocols.modbus_test.ModbusTestSlaveParams;
import ru.entel.smiu.datadealer.protocols.registers.RegType;
import ru.entel.smiu.datadealer.protocols.service.InvalidProtocolTypeException;
import ru.entel.smiu.datadealer.protocols.service.ProtocolMaster;
import ru.entel.smiu.datadealer.protocols.service.ProtocolMasterParams;
import ru.entel.smiu.datadealer.utils.InvalidJSONException;
import ru.entel.smiu.datadealer.utils.JSONNaturalDeserializer;
import ru.entel.smiu.datadealer.utils.JSONUtils;

import java.util.*;

/**
 * Configurator - Слушает MQTT (ветка smiu/DD/updateConfig), хранит конфигурацию в JSON.
 * По запросу парсит JSON конфиг и возвращает коллекцию объектов ProtocolMaster
 * @author Мацепура Артем
 * @version 0.2
 */
public class Configurator implements MqttCallback {
    private static final Logger logger = Logger.getLogger(Configurator.class);
    public static List<Protocol> protocols;

    private Engine engine;

    /**
     * Основная переменная для работы с MQTT
     */
    private MqttClient client;

    /**
     * Ветка, которую слушает MQTT client для обновления конфигов
     */
    private final String CONFIG_TOPIC = "smiu/DD/updateConfig";

    /**
     * Строковый идентификатор данного клиента
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final String CLIENT_ID = "DD-config";

    public Configurator(Engine engine) {
        this.engine = engine;
        mqttInit();
    }

    /**
     * Основной метод класса. Парсит jsonConfig и создает возвращаемый словарь
     * @return словарь: ключ - название мастера протокола; значение - объект ProtocolMaster, готовый к запуску.
     * @throws InvalidJSONException Невалидный JSON
     * @throws InvalidProtocolTypeException Неизвестный типа протокола
     */
    public synchronized Map<String, ProtocolMaster> getProtocolMasters() throws InvalidJSONException, InvalidProtocolTypeException {
        Map<String, ProtocolMaster> res = new HashMap<>();

        protocols = DataHelper.getInstance().getAllProtocols();
        Gson gson = new GsonBuilder().registerTypeAdapter(Object.class, new JSONNaturalDeserializer()).create();

        for (Protocol protocol : protocols) {
            String jsonConfig = protocol.getProtocolSettings();
            if (!JSONUtils.isJSONValid(jsonConfig))
                throw new InvalidJSONException("Invalid json");

            Map protocolParams = (Map) gson.fromJson(jsonConfig, Object.class);

            switch (protocol.getType()) {
                case "MODBUS_RTU_MASTER": {
                    String masterName = protocol.getName();
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

                    for (Device device : protocol.getDevices()) {
                        String jsonDevConf = device.getDeviceSettings();
                        if (!JSONUtils.isJSONValid(jsonDevConf))
                            throw new InvalidJSONException("Invalid json");

                        Map slaveParams = (Map) gson.fromJson(jsonDevConf, Object.class);

                        int unitID = ((Double) slaveParams.get("unitId")).intValue();
                        DeviceBlank deviceBlank = device.getDeviceBlank();
                        for (TagBlank tagBlank : deviceBlank.getTagBlanks()) {
                            String tagParams[] = tagBlank.getTagId().split(":");
                            ModbusFunction mbFunc = ModbusFunction.valueOf(String.valueOf(tagParams[0]));
                            RegType regType = RegType.valueOf(String.valueOf(tagParams[1]));
                            int offset = Integer.valueOf(tagParams[2]);
                            int length = 1;
                            int transDelay = tagBlank.getDelay();
                            String slaveName = tagBlank.getTagName();

                            ModbusSlaveParams sp = new ModbusSlaveParams(unitID, mbFunc, regType, offset,
                                    length, transDelay);
                            master.addSlave(new ModbusSlaveRead(slaveName, sp, device, tagBlank));
                        }

                    }
                    res.put(masterName, master);
                    break;
                }
                case "MODBUS_TEST": {
                    String protocolName = protocol.getName();
                    ProtocolMasterParams masterParams = null;
                    ModbusTestMaster testMaster = new ModbusTestMaster(protocolName, masterParams);
                    for (Device device : protocol.getDevices()) {
                        DeviceBlank deviceBlank = device.getDeviceBlank();
                        for (TagBlank tagBlank : deviceBlank.getTagBlanks()) {
                            String slaveName = tagBlank.getTagName();
                            ModbusTestSlaveParams sp = new ModbusTestSlaveParams();
                            testMaster.addSlave(new ModbusTestSlave(slaveName, sp, device, tagBlank));
                        }
                    }
                    System.out.println();
                    res.put(protocolName, testMaster);
                    break;
                }
                default:
                    throw new InvalidProtocolTypeException("Invalid protocol type - " + protocol.getType());
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
