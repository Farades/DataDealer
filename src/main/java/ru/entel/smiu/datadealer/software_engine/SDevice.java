package ru.entel.smiu.datadealer.software_engine;

import ru.entel.smiu.datadealer.db.entity.DeviceEntity;
import ru.entel.smiu.datadealer.db.entity.TagBlankEntity;
import ru.entel.smiu.datadealer.engine.Engine;

import java.util.HashMap;
import java.util.Map;

public class SDevice {
    private DeviceEntity deviceEntity;
    private Map<String, Value> values = new HashMap<>();

    public SDevice(DeviceEntity deviceEntity) {
        this.deviceEntity = deviceEntity;
        init();
    }

    private synchronized void init() {
        updateValues();
    }

    public synchronized void update() {
        updateValues();
    }

    private synchronized void updateValues() {
        for (TagBlankEntity tagBlankEntity : deviceEntity.getTagBlankEntities()) {
            Value value = new Value(Engine.getInstance().getHardwareEngine().getRegisterByID(tagBlankEntity.getTagBinding()));
            values.put(tagBlankEntity.getTagDescr(), value);
        }
    }

    @Override
    public String toString() {
        return "SDevice{" +
                "values=" + values +
                '}';
    }
}
