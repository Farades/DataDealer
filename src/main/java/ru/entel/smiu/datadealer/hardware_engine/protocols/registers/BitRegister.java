package ru.entel.smiu.datadealer.hardware_engine.protocols.registers;

/**
 * BitRegister - регистр, хранящий в себе 1 бит в переменной boolean
 */
public class BitRegister extends AbstractRegister {

    public BitRegister(boolean value) {
        setValue(value);
    }

    public void setValue(boolean value) {
        this.value = value ? 1 : 0;
    }
}
