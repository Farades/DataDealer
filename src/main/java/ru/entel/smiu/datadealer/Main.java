package ru.entel.smiu.datadealer;

import ru.entel.smiu.datadealer.engine.Engine;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
//        Engine engine = new Engine();
//        engine.configure();
//        engine.run();
//        HardwareEngine hardwareEngine = new HardwareEngine();
//        hardwareEngine.restart();

        Engine.getInstance().reConfigure();
        Engine.getInstance().start();
    }
}
