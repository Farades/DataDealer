package ru.entel.smiu.datadealer.hardware_engine.protocols.registers;

public class ErrRegister extends AbstractRegister {
    private String error;

    public ErrRegister(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return error;
    }
}
