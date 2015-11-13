package ru.entel.smiu.datadealer.protocols.registers;

/**
 * Created by farades on 11.11.15.
 */
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
