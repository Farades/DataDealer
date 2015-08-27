package ru.entel.protocols.registers;

/**
 * Created by farades on 07.05.2015.
 */
public class AbstractRegister {
    protected int regNumb;
    protected Number value;

    public int getRegNumb() {
        return regNumb;
    }

    public void setRegNumb(int regNumb) {
        this.regNumb = regNumb;
    }

    public Number getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
