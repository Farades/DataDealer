package ru.entel.smiu.msg;

/**
 * Created by farades on 05.09.15.
 */
public class MessageServiceFactory {
    public static MessageService getMessageService(MessageServiceType type) {
        MessageService res = null;
        switch (type) {
            case MQTT:
                res = MqttService.getInstance();
                break;
            case EVENT_BUS:
                break;
            default:
                break;
        }
        return res;
    }
}
