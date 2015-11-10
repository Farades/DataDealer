package ru.entel.smiu.datadealer.msg;

/**
 * MessageService - интерфейс сервиса отправки сообщений
 */
public interface MessageService {
    /**
     * Отправка сообщения
     * @param descr - Описание. Например TopicName для MQTT, ...
     * @param data  - Данные. Обычно в формате JSON
     */
    void send(String descr, String data);
}
