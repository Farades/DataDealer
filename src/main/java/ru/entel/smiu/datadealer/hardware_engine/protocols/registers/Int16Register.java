package ru.entel.smiu.datadealer.hardware_engine.protocols.registers;

/**
 * Created by farades on 07.05.2015.
 */
public class Int16Register extends AbstractRegister {

    public Int16Register(int value) {
        setValue(value);
    }

    public Number getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
