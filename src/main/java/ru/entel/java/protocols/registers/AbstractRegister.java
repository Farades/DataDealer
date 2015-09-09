package ru.entel.java.protocols.registers;

/**
 * AbstractRegister - абстрактный класс. Родитель всех регистров. Нужен для использования полиморфизма
 */
public abstract class AbstractRegister {
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
