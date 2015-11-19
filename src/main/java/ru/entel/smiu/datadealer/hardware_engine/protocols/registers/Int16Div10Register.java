package ru.entel.smiu.datadealer.hardware_engine.protocols.registers;

public class Int16Div10Register extends AbstractRegister {

    public Int16Div10Register(int value) {
        setValue(value);
    }

    public Number getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = Float.valueOf(value / 10.0f);
    }
}
