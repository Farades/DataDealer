package ru.entel.datadealer.msg;

import ru.entel.protocols.registers.AbstractRegister;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Класс ModbusDataEvent - потом класса Event.
 * Служит дял передачи информации из ModbusSlaveRead в EventBusService.
 * В последующем обрабатываются handler в объектах конечных устройств.
 * @author Мацепура Артем
 * @version 0.3
 */
public class ModbusDataEvent {
    private String ownerMaster;
    private String ownerSlave;
    private Map<Integer, AbstractRegister> data;
    private LocalDateTime timeCreate;

    public ModbusDataEvent(String ownerMaster, String ownerSlave, Map<Integer, AbstractRegister> data) {
        this.timeCreate = LocalDateTime.now();
        this.ownerMaster = ownerMaster;
        this.ownerSlave = ownerSlave;
        this.data = data;
    }

    public String getOwnerMaster() {
        return ownerMaster;
    }

    public String getOwnerSlave() {
        return ownerSlave;
    }

    public String getOwnerID() {
        return this.ownerMaster + ":" + this.ownerSlave;
    }

    public LocalDateTime getTimeCreate() {
        return timeCreate;
    }

    public Map<Integer, AbstractRegister> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ModbusDataEvent{" +
                "ownerMaster='" + ownerMaster + '\'' +
                ", ownerSlave='" + ownerSlave + '\'' +
                ", data=" + data +
                ", timeCreate=" + timeCreate +
                '}';
    }
}
