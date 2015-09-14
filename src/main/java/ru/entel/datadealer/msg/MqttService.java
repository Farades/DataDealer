package ru.entel.datadealer.msg;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Класс MqttService - Класс-синглтон для отправки сообщений по MQTT
 * @author Мацепура Артем
 * @version 0.3
 */
public class MqttService implements MessageService {
    /**
     * Единственный объект класса MqttService
     */
    private static MqttService instance;

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
    public MqttClient client;

    /**
     * Реализация паттерна Синглтон
     * @return Единственный объект класса MqttService
     */
    public static synchronized MqttService getInstance() {
        if (instance == null) {
            instance = new MqttService();
        }
        return instance;
    }

    private MqttService() {
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
    @Override
    public synchronized void send(String topicName, String data) {
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
