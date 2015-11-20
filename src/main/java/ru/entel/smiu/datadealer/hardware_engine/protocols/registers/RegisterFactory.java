package ru.entel.smiu.datadealer.hardware_engine.protocols.registers;

public class RegisterFactory {
    public static synchronized AbstractRegister getRegisterByType(RegType regType) {
        AbstractRegister res = null;

        switch (regType) {
            case INT16 :
                res = new Int16Register(0);
                break;

            case BIT :
                res = new BitRegister(false);
                break;

            case FLOAT32 :
                res = new Float32Register(0, 0);
                break;

            case INT16DIV10:
                res = new Int16Div10Register(0);
                break;

            case INT16DIV100 :
                res = new Int16Div100Register(0);
                break;
        }
        return res;
    }
}
