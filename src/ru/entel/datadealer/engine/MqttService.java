package ru.entel.datadealer.engine;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Класс MqttService - Статический класс для отправки сообщений
 * @author Мацепура Артем
 * @version 0.2
 */
public class MqttService {
    /**
     * QOS - Quality of Service (0, 1, 2)
     */
    public static final int QOS = 0;

    /**
     * BROKER_URL - Адрес и порт брокер-сервера MQTT
     */
    public static final String BROKER_URL = "tcp://localhost:1883";

    /**
     * CLIENT_ID - ID клиента MqttService
     */
    public static final String CLIENT_ID = "DD-service";

    /**
     * Основной объект для работы с MQTT
     */
    public static MqttClient client;

    static {
        try {
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);
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
