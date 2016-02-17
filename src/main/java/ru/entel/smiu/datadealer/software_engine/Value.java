package ru.entel.smiu.datadealer.software_engine;

import ru.entel.smiu.datadealer.db.entity.TagBlankEntity;
import ru.entel.smiu.datadealer.hardware_engine.protocols.registers.AbstractRegister;

/**
 * Value - класс, объединяющий конкретный регистр и шаблон тега (описание этого регистра).
 * @author Мацепура Артем
 * @version 0.2
 */
public class Value {
    private AbstractRegister register;
    private TagBlankEntity tagBlankEntity;

    public Value(AbstractRegister register, TagBlankEntity tagBlankEntity) {
        this.register = register;
        this.tagBlankEntity = tagBlankEntity;
    }

    public TagBlankEntity getTagBlankEntity() {
        return tagBlankEntity;
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
