package ru.entel.smiu.datadealer.utils;

/**
 * Исключение, сообщающее о не валидности JSON
 * @author Мацепура Артем
 * @version 0.2
 */
public class InvalidJSONException extends Exception {

    public InvalidJSONException(String message) {
        super(message);
    }
}
