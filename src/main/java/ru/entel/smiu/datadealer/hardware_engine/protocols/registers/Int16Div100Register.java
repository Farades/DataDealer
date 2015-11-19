package ru.entel.smiu.datadealer.hardware_engine.protocols.registers;

public class Int16Div100Register extends AbstractRegister {

    public Int16Div100Register(int value) {
        setValue(value);
    }

    public Number getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = Float.valueOf(value / 100.0f);
    }
}
