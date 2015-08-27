package ru.entel.datadealer.engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import ru.entel.protocols.modbus.ModbusFunction;
import ru.entel.protocols.modbus.rtu.master.ModbusMaster;
import ru.entel.protocols.modbus.rtu.master.ModbusMasterParams;
import ru.entel.protocols.modbus.rtu.master.ModbusSlaveParams;
import ru.entel.protocols.modbus.rtu.master.ModbusSlaveRead;
import ru.entel.protocols.registers.RegType;
import ru.entel.protocols.service.InvalidProtocolTypeException;
import ru.entel.protocols.service.ProtocolMaster;
import ru.entel.utils.InvalidJSONException;
import ru.entel.utils.JSONNaturalDeserializer;
import ru.entel.utils.JSONUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Configurator - Слушает MQTT (ветка smiu/DD/updateConfig), хранит конфигурацию в JSON.
 * По запросу парсит JSON конфиг и возвращает коллекцию объектов ProtocolMaster
 */
public class Configurator implements MqttCallback {
    /**
     * Переменные необходимые для работы с MQTT
     */
    private MqttClient client;
    private MqttConnectOptions connectOptions;

    /**
     * Ветка, которую слушает MQTT client для обновления конфигов
     */
    private final String CONFIG_TOPIC = "smiu/DD/updateConfig";

    /**
     * Флаг, управляющий потоком слушателя MQTT
     */
    private volatile boolean subscribeRunning = true;

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
        Map jsonParams = (Map) gson.fromJson(jsonConfig.toString(), Object.class);
        //ArrayList, хранящий информацию обо всех протоколах (если их больше 1)
        ArrayList protocolsParams = (ArrayList) jsonParams.get("protocols");

        //Цикл по всем протоколам
        for (int i = 0; i < protocolsParams.size(); i++) {
            //Словарь, хранящий информацию про конкретный i-ый ProtocolMaster
            Map protocolParams = (Map)protocolsParams.get(i);
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
                    for (int j = 0; j < slaves.size(); j++) {
                        //Словарь хранящий информацию про конкретный Slave
                        Map slaveParams = (Map)slaves.get(i);

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
     * Инициализация MQTT клиента.
     */
    private void mqttInit() {
        try {
            connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(MqttService.CLEAN_SESSION);
            client = new MqttClient(MqttService.BROKER_URL, "DD-config", new MemoryPersistence());
            client.setCallback(this);
            subscribe();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * Подпись и постоянное прослушивание ветки CONFIG_TOPIC в отдельном потоке.
     */
    private void subscribe() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.connect();
                    client.subscribe(CONFIG_TOPIC, MqttService.QOS);
                    while (subscribeRunning);
                } catch (MqttException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        client.disconnect();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * Метод, вызываемый при получении нового конфига по MQTT.
     * @param jsonConfig новый конфиг в JSON
     * @throws InvalidJSONException Невалидный JSON
     */
    private void updateConfig(String jsonConfig) throws InvalidJSONException {
        if (JSONUtils.isJSONValid(jsonConfig)) {
            String time = new Timestamp(System.currentTimeMillis()).toString();
            System.out.println(time + ": Update config");
            this.jsonConfig = jsonConfig;
        } else {
            throw new InvalidJSONException("Invalid json");
        }
     }

    /**
     * Перегруженный CallBack-метод интерфейса MqttCallback
     * Вызывается при потери соединения с MQTT сервером
     * @param throwable
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
}
