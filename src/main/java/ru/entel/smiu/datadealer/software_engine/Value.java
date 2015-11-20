package ru.entel.smiu.datadealer.software_engine;

import ru.entel.smiu.datadealer.hardware_engine.protocols.registers.AbstractRegister;

public class Value {
    private AbstractRegister register;

    public Value(AbstractRegister register) {
        this.register = register;
    }

    public AbstractRegister getRegister() {
        return register;
    }

    @Override
    public String toString() {
        return "Value{" +
                "register=" + register +
                '}';
    }
}
