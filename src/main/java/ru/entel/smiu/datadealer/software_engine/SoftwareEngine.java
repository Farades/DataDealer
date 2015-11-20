package ru.entel.smiu.datadealer.software_engine;

import org.apache.log4j.Logger;
import ru.entel.smiu.datadealer.db.entity.DeviceEntity;
import ru.entel.smiu.datadealer.db.util.DataHelper;
import ru.entel.smiu.datadealer.engine.Engine;

import java.util.*;

public class SoftwareEngine {
    private static final Logger logger = Logger.getLogger(SoftwareEngine.class);

    private Map<String, SDevice> devices = new HashMap<>();

    private Timer updateTimer;


    public synchronized void configure() {
        List<DeviceEntity> deviceEntities = DataHelper.getInstance().getAllDevices();

        for (DeviceEntity deviceEntity : deviceEntities) {
            SDevice device = new SDevice(deviceEntity);
            devices.put(deviceEntity.getName(), device);
        }

        logger.debug("Software Engine configured");
    }

    public synchronized void start() {
        updateTimer = new Timer("Software Engine");
        Scheduller scheduller = new Scheduller();
        updateTimer.scheduleAtFixedRate(scheduller, 5000, 1000);
    }

    public synchronized void stop() {

    }

    private class Scheduller extends TimerTask {
        @Override
        public void run() {
//            for (SDevice device : devices.values()) {
//                device.update();
//            }

//            System.out.println(devices);

//            System.out.println(Engine.getInstance().getHardwareEngine().getProtocols());
        }
    }
}
