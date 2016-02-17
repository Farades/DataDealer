package ru.entel.smiu.datadealer.software_engine;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * AlarmUtil - класс-синглтон. Возвращает движок JavaScript для обработки скриптов аварий
 */
public class AlarmUtil {
    private static AlarmUtil instance;
    private ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("js");

    public static synchronized AlarmUtil getInstance() {
        if (instance == null) {
            instance = new AlarmUtil();
        }
        return instance;
    }

    public ScriptEngine getScriptEngine() {
        return scriptEngine;
    }

    private AlarmUtil() {

    }
}
