package ru.entel.smiu.datadealer.protocols.registers;

import com.ghgande.j2mod.modbus.util.ModbusUtil;

import java.nio.ByteBuffer;

/**
 * Created by farades on 07.05.2015.
 */
public class Float32Register extends AbstractRegister {
    private RegType regType;
    private Integer tempValue1;
    private Integer tempValue2;

    public Float32Register(int regNumb, int value1, int value2) {
        this.regType = RegType.FLOAT32;
        this.regNumb = regNumb;
        this.tempValue1 = value1;
        this.tempValue2 = value2;
        convertTwoIntToFloat();
    }

    private void convertTwoIntToFloat() {
        byte[] tempValue1Bytes = ByteBuffer.allocate(4).putInt(tempValue1).array();
        byte[] tempValue2Bytes = ByteBuffer.allocate(4).putInt(tempValue2).array();
        byte[] tempFloatValueBytes = new byte[4];
        System.arraycopy(tempValue1Bytes, 2, tempFloatValueBytes, 0, 2);
        System.arraycopy(tempValue2Bytes, 2, tempFloatValueBytes, 2, 2);
        this.value = ModbusUtil.registersToFloat(tempFloatValueBytes);
    }
}