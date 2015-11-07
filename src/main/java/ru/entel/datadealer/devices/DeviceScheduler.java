package ru.entel.datadealer.devices;

import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;
import ru.entel.datadealer.db.entity.*;
import ru.entel.datadealer.db.entity.Device;
import ru.entel.datadealer.msg.EventBusService;
import ru.entel.datadealer.msg.ModbusDataEvent;

import java.io.Serializable;

/**
 * Created by farades on 07.11.15.
 */
@Listener(references= References.Strong)
public class DeviceScheduler implements Serializable {

    private ru.entel.datadealer.db.entity.Device device;

    public DeviceScheduler(Device device) {
        this.device = device;
        EventBusService.getModbusBus().subscribe(this);
    }


    @Handler
    public void handleModbusDataEvent(ModbusDataEvent evt) {
        System.out.println(evt);
    }
}
