package ru.entel.smiu.datadealer.engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import ru.entel.smiu.datadealer.protocols.service.ProtocolMaster;
import ru.entel.smiu.datadealer.protocols.service.ProtocolSlave;
import ru.entel.smiu.msg.DeviceBAO;
import ru.entel.smiu.msg.MqttService;
import ru.entel.smiu.msg.StatePackage;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

public class MqttEngine extends TimerTask implements MqttCallback {
    private Engine engine;

    private static final Logger logger = Logger.getLogger(MqttEngine.class);

    private MqttClient client;

    private Gson gson;

    private final String CLIENT_ID = "smiu-dd";

    private final String MAIN_TOPIC = "smiu/DD/msg";
    private final String DEVICES_OUT_TOPIC = "smiu/DD/devices/out";

    public MqttEngine(Engine engine) {
        this.engine = engine;
        mqttInit();
        GsonBuilder builder = new GsonBuilder();
        gson = builder.create();
    }

    @Override
    public synchronized void run() {
        Map<String, DeviceBAO> allDevicesByName = new HashMap<>();
        for (ProtocolMaster master : engine.getProtocolMasterMap().values()) {
            for (ProtocolSlave slave : master.getSlaves().values()) {
                if (allDevicesByName.containsKey(slave.getDevice().getName())) {
                    DeviceBAO deviceBAO = allDevicesByName.get(slave.getDevice().getName());
                    deviceBAO.addChannel(slave.getTagBlank().getTagDescr(), slave.getData().toString());
                } else {
                    DeviceBAO deviceBAO = new DeviceBAO();
                    deviceBAO.addChannel(slave.getTagBlank().getTagDescr(), slave.getData().toString());
                    //Если для данного канала есть активные аварии
                    if (engine.getAlarmsChecker().getActiveAlarms().containsKey(slave.getDevice())) {
                        deviceBAO.setActiveAlarms(engine.getAlarmsChecker().getActiveAlarms().get(slave.getDevice()));
                    }
                    allDevicesByName.put(slave.getDevice().getName(), deviceBAO);
                }
            }
        }

        StatePackage statePackage = new StatePackage(allDevicesByName);
        String statePackageJson = gson.toJson(statePackage);
        Charset.forName("UTF-8").encode(statePackageJson);
//        logger.debug(gson.toJson(statePackage));
        send(DEVICES_OUT_TOPIC, statePackageJson);
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