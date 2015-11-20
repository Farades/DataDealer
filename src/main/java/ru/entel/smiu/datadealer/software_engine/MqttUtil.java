package ru.entel.smiu.datadealer.software_engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import ru.entel.smiu.datadealer.engine.Engine;
import ru.entel.smiu.msg.DeviceBAO;
import ru.entel.smiu.msg.MqttService;
import ru.entel.smiu.msg.StatePackage;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

public class MqttUtil extends TimerTask implements MqttCallback {
    private static MqttUtil instance;

    private static final Logger logger = Logger.getLogger(MqttUtil.class);

    private MqttClient client;

    private Gson gson;

    private final String CLIENT_ID = "smiu-dd";

    private final String MAIN_TOPIC = "smiu/DD/msg";
    private final String DEVICES_OUT_TOPIC = "smiu/DD/devices/out";

    public static synchronized MqttUtil getInstance() {
        if (instance == null) {
            instance = new MqttUtil();
        }
        return instance;
    }

    private MqttUtil() {
        mqttInit();
        GsonBuilder builder = new GsonBuilder();
        gson = builder.setPrettyPrinting().create();
    }

    public synchronized void sendDevices() {
        Map<String, DeviceBAO> allDevicesByName = new HashMap<>();

        for (Map.Entry<String, SDevice> entry : Engine.getInstance().getSoftwareEngine().getDevices().entrySet()) {
            DeviceBAO deviceBAO = new DeviceBAO();
            for (Map.Entry<String, Value> entryValues : entry.getValue().getValues().entrySet()) {
                deviceBAO.addChannel(entryValues.getKey(), entryValues.getValue().getRegister().toString());
            }
            deviceBAO.setActiveAlarms(entry.getValue().getActiveAlarms());
            allDevicesByName.put(entry.getKey(), deviceBAO);
        }
        StatePackage statePackage = new StatePackage(allDevicesByName);

        String json = gson.toJson(statePackage);
        Charset.forName("UTF-8").encode(json);
        send(DEVICES_OUT_TOPIC, json);
    }

    @Override
    public synchronized void run() {


//        Map<String, DeviceBAO> allDevicesByName = new HashMap<>();
//        for (ProtocolMaster master : engine.getProtocolMasterMap().values()) {
//            for (ProtocolSlave slave : master.getSlaves().values()) {
//                if (slave.getData() == null) {
//                    return;
//                }
//                if (allDevicesByName.containsKey(slave.getDeviceEntity().getName())) {
//                    DeviceBAO deviceBAO = allDevicesByName.get(slave.getDeviceEntity().getName());
//                    deviceBAO.addChannel(slave.getTagBlankEntity().getTagDescr(), slave.getData().toString());
//                } else {
//                    DeviceBAO deviceBAO = new DeviceBAO();
//                    deviceBAO.addChannel(slave.getTagBlankEntity().getTagDescr(), slave.getData().toString());
//                    //Если для данного канала есть активные аварии
//                    if (engine.getAlarmsChecker().getActiveAlarms().containsKey(slave.getDeviceEntity())) {
//                        deviceBAO.setActiveAlarms(engine.getAlarmsChecker().getActiveAlarms().get(slave.getDeviceEntity()));
//                    }
//                    allDevicesByName.put(slave.getDeviceEntity().getName(), deviceBAO);
//                }
//            }
//        }

//        StatePackage statePackage = new StatePackage(allDevicesByName);
//        logger.debug(gson.toJson(statePackage));
//        send(DEVICES_OUT_TOPIC, statePackageJson);
    }

    private void mqttInit() {
        try {
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);
            client = new MqttClient(MqttService.BROKER_URL, CLIENT_ID, new MemoryPersistence());

            client.setCallback(this);
            client.connect(connectOptions);
            client.subscribe(MAIN_TOPIC, 0);
        } catch (MqttException e) {
            logger.error("Ошибка в функции mqttInit(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    private synchronized void send(String topicName, String data) {
        try {
            //Настройка топика и сообщения
            MqttTopic topic = client.getTopic(topicName);
            MqttMessage message = new MqttMessage(data.getBytes());
            message.setQos(MqttService.QOS);

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
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        if (s.equals(MAIN_TOPIC)) {
            switch (mqttMessage.toString()) {
            }
        }

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        client.disconnect();
    }
}
