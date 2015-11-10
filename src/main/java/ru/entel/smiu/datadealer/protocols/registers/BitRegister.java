package ru.entel.smiu.datadealer.protocols.registers;

/**
 * BitRegister - регистр, хранящий в себе 1 бит в переменной boolean
 */
public class BitRegister extends AbstractRegister {
    private RegType regType;

    public BitRegister(int regNumb, boolean value) {
        this.regType = RegType.BIT;
        this.regNumb = regNumb;
        this.value = value ? 1 : 0;
    }
}
