package ru.entel.java.protocols.registers;

/**
 * Created by farades on 12.05.2015.
 */
public class Int16Div100Register extends AbstractRegister {
    private RegType regType;

    public Int16Div100Register(int regNumb, int value) {
        this.regNumb = regNumb;
        this.regType = RegType.INT16DIV100;
        this.value = Float.valueOf(value / 100.0f);
    }

    public RegType getRegType() {
        return regType;
    }

    public Number getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
