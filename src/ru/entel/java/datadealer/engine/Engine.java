package ru.entel.java.datadealer.engine;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import ru.entel.java.datadealer.msg.MqttService;
import ru.entel.java.protocols.service.InvalidProtocolTypeException;
import ru.entel.java.protocols.service.ProtocolMaster;
import ru.entel.java.utils.InvalidJSONException;

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
     * Словарь, содержащий все ProtocolMaster'ы, готовые к запуску
     */
    private Map<String, ProtocolMaster> protocolMasterMap;

    /**
     * Объект, занимающийся конфигурированием словаря protocolMasterMap. Получает данные от MQTT сервера.
     */
    private Configurator configurator;

    public Engine() {
        configurator = new Configurator();
        mqttInit();
    }

    /**
     * Запуск опроса всех ProtocolMaster'ов в отдельных потоках
     */
    public void run() {
        try {
            protocolMasterMap = configurator.getProtocolMasters();
        } catch (InvalidProtocolTypeException | InvalidJSONException e) {
            logger.error("Ошибка при создании ProtocolMaster'ов в конфигураторе: " + e.getMessage());
            e.printStackTrace();
        }

        for (ProtocolMaster pm : protocolMasterMap.values()) {
            new Thread(pm, pm.getName()).start();
            logger.debug(pm.getName() + " started");
        }
    }

    /**
     * Остановка опроса всех ProtocolMaster'ов
     */
    public void stop() {
        protocolMasterMap.forEach((k, v) -> v.stop());
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
        } catch (MqttException e) {
            logger.error("Ошибка в функции mqttInit(): " + e.getMessage());
            e.printStackTrace();
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
        if (s.equals(ENGINE_TOPIC)) {
            switch (mqttMessage.toString()) {
                case "run":
                    run();
                    logger.debug("Data Dealer running.");
                    break;
                case "stop":
                    stop();
                    logger.debug("Data Dealer stopping.");
                    break;
            }
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
