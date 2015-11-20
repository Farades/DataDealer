package ru.entel.smiu.datadealer.engine;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import ru.entel.smiu.datadealer.hardware_engine.HardwareEngine;
import ru.entel.smiu.datadealer.software_engine.SoftwareEngine;
import ru.entel.smiu.datadealer.utils.InvalidJSONException;
import ru.entel.smiu.msg.MqttService;

import java.util.*;

/**
 * Класс Engine - основной класс приложения DataDealer
 * Занимается инициализацией основных объектов программы (все ProtocolMatser'ы, Configurator),
 * запуском опроса всех мастеров в отдельных потоках, прослушиванием MQTT ветки ENGINE_TOPIC.
 * @author Мацепура Артем
 * @version 0.2
 */
public class Engine implements MqttCallback {
    private static final Logger logger = Logger.getLogger(Engine.class);

    private static Engine instance;

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

    private HardwareEngine hardwareEngine;
    private SoftwareEngine softwareEngine;

    private DataSaver ds;
    private AlarmsChecker alarmsChecker;
    private MqttEngine mqttEngine;
    private Timer dataSaverTimer;
    private Timer alarmsCheckerTimer;
    private Timer messageTimer;

    public static synchronized Engine getInstance() {
        if (instance == null) {
            instance = new Engine();
        }
        return instance;
    }

    private Engine() {
        mqttInit();
        System.out.println("DataDealer started! Wait for command by MQTT.");;
        System.out.println("Topic: \"smiu/DD/engine\"");

    }

    public synchronized void run() {
        try {

            dataSaverTimer = new Timer("Data Saver");
            ds = new DataSaver(this);
            dataSaverTimer.schedule(ds, 5000, 5000);

            alarmsCheckerTimer = new Timer("Alarms Checker");
            alarmsChecker = new AlarmsChecker(this);
            alarmsCheckerTimer.schedule(alarmsChecker, 5000, 1000);

            messageTimer = new Timer("Mqtt Engine");
            mqttEngine = new MqttEngine(this);
            messageTimer.schedule(mqttEngine, 5000, 1000);

            logger.debug("Data Dealer running.");
        } catch (RuntimeException ex) {
            logger.error("DataDelaer running before update config: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public synchronized void stop() {
        if (hardwareEngine != null) {
            hardwareEngine.stop();
        }

        if (softwareEngine != null) {
            softwareEngine.stop();
        }
    }

    public synchronized void start() {
        if (hardwareEngine != null) {
            hardwareEngine.start();
        }

        if (softwareEngine != null) {
            softwareEngine.start();
        }
    }

    private synchronized void configure() {
        try {
            hardwareEngine = new HardwareEngine();
            hardwareEngine.configure();

            softwareEngine = new SoftwareEngine();
            softwareEngine.configure();
        } catch (InvalidJSONException e) {
            e.printStackTrace();
        }
    }

    public synchronized void reConfigure() {
        if (ds != null && dataSaverTimer != null) {
            dataSaverTimer.cancel();
            dataSaverTimer.purge();
            ds = null;
            dataSaverTimer = null;
        }

        if (alarmsChecker != null && alarmsCheckerTimer != null) {
            alarmsCheckerTimer.cancel();
            alarmsCheckerTimer.purge();
            alarmsChecker = null;
            alarmsCheckerTimer = null;
        }
        configure();
        logger.debug("Data Dealer reconfigure.");
    }

    public HardwareEngine getHardwareEngine() {
        return hardwareEngine;
    }

    /**
     * Инициализация MQTT клиента. Подпись на ветку ENGINE_TOPIC
     */
    private synchronized void mqttInit() {
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
                    break;
                case "stop":
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
