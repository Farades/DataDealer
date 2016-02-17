package ru.entel.smiu.datadealer.utils;

import com.google.gson.*;
import ru.entel.smiu.datadealer.hardware_engine.protocols.registers.AbstractRegister;

import java.lang.reflect.Type;

public class RegisterSerializer implements JsonSerializer<AbstractRegister> {
    @Override
    public JsonElement serialize(AbstractRegister abstractRegister, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(abstractRegister.getValue());
    }
}
