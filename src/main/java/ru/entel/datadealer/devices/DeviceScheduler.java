package ru.entel.datadealer.devices;

import ru.entel.datadealer.db.entity.*;
import ru.entel.datadealer.db.entity.Device;

/**
 * Created by farades on 07.11.15.
 */
public class DeviceScheduler implements Runnable {

    private ru.entel.datadealer.db.entity.Device device;

    public DeviceScheduler(Device device) {
        this.device = device;
    }


    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                System.out.println("Device Scheduller");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
