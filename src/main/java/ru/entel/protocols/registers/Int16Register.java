package ru.entel.protocols.registers;

/**
 * Created by farades on 07.05.2015.
 */
public class Int16Register extends AbstractRegister {
    private RegType regType;

    public Int16Register(int regNumb, int value) {
        this.regNumb = regNumb;
        this.regType = RegType.INT16;
        this.value = value;
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
