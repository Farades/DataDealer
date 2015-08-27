package ru.entel.datadealer.engine;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import ru.entel.protocols.service.InvalidProtocolTypeException;
import ru.entel.protocols.service.ProtocolMaster;
import ru.entel.utils.InvalidJSONException;

import java.util.Map;

public class Engine implements MqttCallback {
    private MqttClient client;
    private MqttConnectOptions connectOptions;
    private final String ENGINE_TOPIC = "smiu/DD/engine";
    private volatile boolean subscribe_running = true;
    private Map<String, ProtocolMaster> protocolMasterMap;
    private Configurator configurator;

    public Engine() {
        configurator = new Configurator();
        mqttInit();
    }

    public void run() {
        try {
            protocolMasterMap = configurator.getProtocolMasters();
        } catch (InvalidJSONException e) {
            e.printStackTrace();
        } catch (InvalidProtocolTypeException e) {
            e.printStackTrace();
        }
        for (ProtocolMaster pm : protocolMasterMap.values()) {
            new Thread(pm).start();
        }
    }

    private void mqttInit() {
        try {
            connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(MqttService.CLEAN_SESSION);
            client = new MqttClient(MqttService.BROKER_URL, "DD-engine", new MemoryPersistence());
            client.setCallback(this);
            subscribe();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void subscribe() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.connect();
                    client.subscribe(ENGINE_TOPIC, MqttService.QOS);
                    while (subscribe_running);
                } catch (MqttException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        client.disconnect();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        if (s.equals(ENGINE_TOPIC)) {
            switch (mqttMessage.toString()) {
                case "run":
                    run();
                    System.out.println("DD start");
                    break;
                case "stop":

                    System.out.println("DD stop");
                    break;
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
