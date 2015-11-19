package ru.entel.smiu.datadealer.hardware_engine.protocols.registers;

/**
 * AbstractRegister - абстрактный класс. Родитель всех регистров. Нужен для использования полиморфизма
 */
public abstract class AbstractRegister {
    protected Number value;
    protected boolean valid;

    public Number getValue() {
        return value;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
