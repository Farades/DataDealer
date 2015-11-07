package ru.entel.datadealer.msg;

import net.engio.mbassy.bus.MBassador;

/**
 * Created by farades on 07.11.15.
 */
public class EventBusService {
    private static MBassador<ModbusDataEvent> modbusBus = new MBassador<ModbusDataEvent>();

    public static MBassador<ModbusDataEvent> getModbusBus() {
        return modbusBus;
    }

    public static void reInit() {
        modbusBus.shutdown();
        modbusBus = new MBassador<ModbusDataEvent>();
    }
}
