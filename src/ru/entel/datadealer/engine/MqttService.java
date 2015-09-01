package ru.entel.datadealer.engine;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Date;

/**
 * Класс MqttService - Статический класс для отправки сообщений
 */
public class MqttService {
    static int count = 0;
    /**
     * QOS - Quality of Service (0, 1, 2)
     */
    public static final int QOS = 0;

    /**
     * BROKER_URL - Адрес и порт брокер-сервера MQTT
     */
    public static final String BROKER_URL = "tcp://localhost:1883";

    /**
     * CLIENT_ID - ID клиента
     */
    public static final String CLIENT_ID = "DD-service";

    /**
     * Очистка сессии
     */
    public static final boolean CLEAN_SESSION = false;

    public static MqttClient client;
    private static MqttConnectOptions connectOptions;

    static {
        try {
            connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(false);
            client = new MqttClient(BROKER_URL, CLIENT_ID, new MemoryPersistence());
            client.connect(connectOptions);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * Отправка сообщения data в ветку topicName
     * @param topicName ветка
     * @param data сообщение
     */
    public static synchronized void publish(String topicName, String data) {
        Date startTime = new Date();
        try {
            //Настройка топика и сообщения
            MqttTopic topic = client.getTopic(topicName);
            MqttMessage message = new MqttMessage(data.getBytes());
            message.setQos(QOS);

            //Отправка сообщения
//            System.out.println("[" + Thread.currentThread().getName() + "] Publish msg: " + data);
            MqttDeliveryToken token = topic.publish(message);

            //Ждем пока сервер подтвердит получение сообщения
            token.waitForCompletion();
        } catch (MqttException e) {
            e.printStackTrace();
        }
}

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        client.disconnect();
    }
}
