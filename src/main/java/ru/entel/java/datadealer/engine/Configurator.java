package ru.entel.java.datadealer.engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import ru.entel.java.datadealer.msg.MqttService;
import ru.entel.java.protocols.modbus.ModbusFunction;
import ru.entel.java.protocols.modbus.rtu.master.ModbusMaster;
import ru.entel.java.protocols.modbus.rtu.master.ModbusMasterParams;
import ru.entel.java.protocols.modbus.rtu.master.ModbusSlaveParams;
import ru.entel.java.protocols.modbus.rtu.master.ModbusSlaveRead;
import ru.entel.java.protocols.registers.RegType;
import ru.entel.java.protocols.service.InvalidProtocolTypeException;
import ru.entel.java.protocols.service.ProtocolMaster;
import ru.entel.java.utils.InvalidJSONException;
import ru.entel.java.utils.JSONNaturalDeserializer;
import ru.entel.java.utils.JSONUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Configurator - Слушает MQTT (ветка smiu/DD/updateConfig), хранит конфигурацию в JSON.
 * По запросу парсит JSON конфиг и возвращает коллекцию объектов ProtocolMaster
 * @author Мацепура Артем
 * @version 0.2
 */
public class Configurator implements MqttCallback {
    private static final Logger logger = Logger.getLogger(Configurator.class);

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

    /**
     * Основный конфиг. Хранится в формате JSON. Получается от MQTT сервера.
     */
    private String jsonConfig = "";

    public Configurator() {
        mqttInit();
    }

    /**
     * Основной метод класса. Парсит jsonConfig и создает возвращаемый словарь
     * @return словарь: ключ - название мастера протокола; значение - объект ProtocolMaster, готовый к запуску.
     * @throws InvalidJSONException Невалидный JSON
     * @throws InvalidProtocolTypeException Неизвестный типа протокола
     */
    public Map<String, ProtocolMaster> getProtocolMasters() throws InvalidJSONException, InvalidProtocolTypeException {
        if (!JSONUtils.isJSONValid(jsonConfig))
            throw new InvalidJSONException("Invalid json");

        Map<String, ProtocolMaster> res = new HashMap<>();

        Gson gson = new GsonBuilder().registerTypeAdapter(Object.class, new JSONNaturalDeserializer()).create();
        Map jsonParams = (Map) gson.fromJson(jsonConfig, Object.class);
        //ArrayList, хранящий информацию обо всех протоколах (если их больше 1)
        ArrayList protocolsParams = (ArrayList) jsonParams.get("protocols");

        //Цикл по всем протоколам
        for (Object protocolParamsObj : protocolsParams) {
            //Словарь, хранящий информацию про конкретный i-ый ProtocolMaster
            Map protocolParams = (Map)protocolParamsObj;
            String protocolType = (String) protocolParams.get("type");

            switch (protocolType) {
                case "MODBUS_MASTER":
                    String masterName = (String) protocolParams.get("name");
                    String portName   = (String) protocolParams.get("portName");
                    String encoding   = (String) protocolParams.get("encoding");
                    String parity     = (String) protocolParams.get("parity");
                    int baudRate      = ((Double)protocolParams.get("baudRate")).intValue();
                    int databits      = ((Double)protocolParams.get("databits")).intValue();
                    int stopbits      = ((Double)protocolParams.get("stopbits")).intValue();
                    int timePause     = ((Double)protocolParams.get("timePause")).intValue();
                    boolean echo      = (Boolean) protocolParams.get("echo");

                    ModbusMasterParams masterParams = new ModbusMasterParams(portName, baudRate, databits, parity,
                            stopbits, encoding, echo, timePause);
                    ModbusMaster master = new ModbusMaster(masterName, masterParams);

                    //Парсинг слейвов для ProtocolMaster'a
                    ArrayList slaves = (ArrayList) protocolParams.get("slaves");
                    for (Object slaveParamsObj : slaves) {
                        //Словарь хранящий информацию про конкретный Slave
                        Map slaveParams = (Map)slaveParamsObj;

                        ModbusFunction mbFunc = ModbusFunction.valueOf(String.valueOf(slaveParams.get("mbFunc")));
                        RegType regType       = RegType.valueOf(String.valueOf(slaveParams.get("mbRegType")));
                        int offset            = ((Double)slaveParams.get("offset")).intValue();
                        int length            = ((Double)slaveParams.get("length")).intValue();
                        int unitID            = ((Double)slaveParams.get("unitId")).intValue();
                        int transDelay        = ((Double)slaveParams.get("transDelay")).intValue();
                        String slaveName      = String.valueOf(slaveParams.get("name"));

                        ModbusSlaveParams sp = new ModbusSlaveParams(unitID, mbFunc, regType, offset,
                                length, transDelay);
                        master.addSlave(new ModbusSlaveRead(slaveName, sp));
                    }
                    res.put(masterName, master);
                    break;
                default:
                    throw new InvalidProtocolTypeException("Invalid protocol type - " + protocolType);
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

    /**
     * Метод, вызываемый при получении нового конфига по MQTT.
     * @param jsonConfig новый конфиг в JSON
     * @throws InvalidJSONException Невалидный JSON
     */
    private void updateConfig(String jsonConfig) throws InvalidJSONException {
        if (JSONUtils.isJSONValid(jsonConfig)) {
            this.jsonConfig = jsonConfig;
            logger.debug("Configurator update config");
        } else {
            throw new InvalidJSONException("Invalid json");
        }
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
            updateConfig(mqttMessage.toString());
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
