package ru.entel.utils;

import com.google.gson.*;
import ru.entel.protocols.registers.AbstractRegister;

import java.lang.reflect.Type;

/**
 * Created by farades on 05.09.15.
 */
public class RegisterSerializer implements JsonSerializer<AbstractRegister> {
    @Override
    public JsonElement serialize(AbstractRegister abstractRegister, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(abstractRegister.getValue());
    }
}
