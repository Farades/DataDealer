package ru.entel.smiu.datadealer.devices;

import java.util.HashMap;

/**
 * Created by farades on 06.11.15.
 */
public abstract class AbstractDevice implements Runnable {
    //Словарь, связывающий определенное значение для мониторинга и с конкретным опрашивающимся устройством и номером его регистра
    protected HashMap<String, Binding> paramsBindings = new HashMap<String, Binding>();
    public AbstractDevice(HashMap<String, Binding> paramsBindings) {
        this.paramsBindings = paramsBindings;
    }

}