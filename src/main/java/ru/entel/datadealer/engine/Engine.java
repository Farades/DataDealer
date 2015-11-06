package ru.entel.datadealer.engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import ru.entel.datadealer.devices.Device;
import ru.entel.datadealer.msg.MessageService;
import ru.entel.datadealer.msg.MessageServiceFactory;
import ru.entel.datadealer.msg.MessageServiceType;
import ru.entel.datadealer.msg.MqttService;
import ru.entel.protocols.registers.AbstractRegister;
import ru.entel.protocols.service.DDPacket;
import ru.entel.protocols.service.InvalidProtocolTypeException;
import ru.entel.protocols.service.ProtocolMaster;
import ru.entel.protocols.service.ProtocolSlave;
import ru.entel.utils.InvalidJSONException;
import ru.entel.utils.RegisterSerializer;

import java.util.Map;

/**
 * Класс Engine - основной класс приложения DataDealer
 * Занимается инициализацией основных объектов программы (все ProtocolMatser'ы, Configurator),
 * запуском опроса всех мастеров в отдельных потоках, прослушиванием MQTT ветки ENGINE_TOPIC.
 * @author Мацепура Артем
 * @version 0.2
 */
public class Engine implements MqttCallback {
    private static final Logger logger = Logger.getLogger(Engine.class);


    /**
     * Объект MessageService предназначен для отправки данных
     */
    private MessageService messageService = MessageServiceFactory.getMessageService(MessageServiceType.MQTT);

    /**
     * Основной объект для работы с MQTT
     */
    private MqttClient client;

    /**
     * Строковый идентификатор данного клиента
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final String CLIENT_ID = "DD-engine";

    /**
     * Ветка, которую слушает MQTT client для управления Engine
     */
    private final String ENGINE_TOPIC = "smiu/DD/engine";

    /**
     * Ветка, которую слушает MQTT client для отдачи данных
     */
    private final String ENGINE_DATA = "smiu/DD/engine/data";

    /**
     * Ветка в которую Engine возвращает данные по конкретному DevID
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final String ENGINE_OUT = "smiu/DD/engine/out";

    /**
     * Объект gson для десериализации и отправки DDPacket по MQTT
     */
    private Gson gson;

    /**
     * Словарь, содержащий все ProtocolMaster'ы, готовые к запуску
     */
    private Map<String, ProtocolMaster> protocolMasterMap;

    /**
     * Словарь, содержащий все устройства
     */
    private Map<String, Device> deviceMap;

    /**
     * Объект, занимающийся конфигурированием словаря protocolMasterMap. Получает данные от MQTT сервера.
     */
    private Configurator configurator;

    public Engine() {
        configurator = new Configurator(this);
        mqttInit();
        this.gson = new GsonBuilder().registerTypeAdapter(AbstractRegister.class, new RegisterSerializer()).create();


    }

    /**
     * Запуск опроса всех ProtocolMaster'ов в отдельных потоках
     */
    public void run() {
        try {
            deviceMap = configurator.getDevices();
            protocolMasterMap = configurator.getProtocolMasters();
            for (ProtocolMaster pm : protocolMasterMap.values()) {
                new Thread(pm, pm.getName()).start();
                logger.debug(pm.getName() + " started");
            }
            for (Device device : deviceMap.values()) {
                new Thread(device, device.getName()).start();
                logger.debug(device.getName() + " started");
            }
            logger.debug("Data Dealer running.");
        } catch (InvalidProtocolTypeException | InvalidJSONException e) {
            logger.error("Ошибка при создании ProtocolMaster'ов в конфигураторе: " + e.getMessage());
            e.printStackTrace();
        } catch (RuntimeException ex) {
            logger.error("DataDelaer running before update config: " + ex.getMessage());
            ex.printStackTrace();
        }


    }

    /**
     * Остановка опроса всех ProtocolMaster'ов
     */
    public void stopEngine() {
        if (protocolMasterMap != null) {
            protocolMasterMap.forEach((k, v) -> v.stopInterview());
            protocolMasterMap = null;
        }
        if (deviceMap != null) {
            deviceMap.forEach((k, v) -> v.stopInterview());
            deviceMap = null;
        }
    }

    /**
     * Инициализация MQTT клиента. Подпись на ветку ENGINE_TOPIC
     */
    private void mqttInit() {
        try {
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);
            client = new MqttClient(MqttService.BROKER_URL, CLIENT_ID, new MemoryPersistence());

            client.setCallback(this);
            client.connect(connectOptions);
            client.subscribe(ENGINE_TOPIC, 0);
            client.subscribe(ENGINE_DATA, 0);
        } catch (MqttException e) {
            logger.error("Ошибка в функции mqttInit(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public DDPacket sendDataByDevID(String devID) {
        DDPacket packet = null;
        try {
            String nodeStr = devID.split(":")[0];
            String devStr = devID.split(":")[1];

            ProtocolMaster node = protocolMasterMap.get(nodeStr);
            ProtocolSlave  dev = node.getSlaves().get(devStr);

            packet = new DDPacket(devID, dev.getData());
//            messageService.send(this.ENGINE_OUT, gson.toJson(packet));
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
        return packet;
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
        if (s.equals(ENGINE_TOPIC)) {
            switch (mqttMessage.toString()) {
                case "run":
                    configurator.updateConfig();
                    run();
                    break;
                case "stop":
                    stopEngine();
                    logger.debug("Data Dealer stopping.");
                    break;
            }
        } else if (s.equals(ENGINE_DATA)) {
            sendDataByDevID(mqttMessage.toString());
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
