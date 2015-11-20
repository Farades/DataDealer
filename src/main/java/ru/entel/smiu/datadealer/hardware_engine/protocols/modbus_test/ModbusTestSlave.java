package ru.entel.smiu.datadealer.hardware_engine.protocols.modbus_test;

import ru.entel.smiu.datadealer.db.entity.DeviceEntity;
import ru.entel.smiu.datadealer.db.entity.TagBlankEntity;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.rtu.master.ProtocolSlave;
import ru.entel.smiu.datadealer.hardware_engine.protocols.registers.AbstractRegister;
import ru.entel.smiu.datadealer.hardware_engine.protocols.registers.ErrRegister;
import ru.entel.smiu.datadealer.hardware_engine.protocols.registers.Int16Div10Register;
import ru.entel.smiu.datadealer.hardware_engine.protocols.service.ProtocolSlaveParams;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by farades on 10.11.15.
 */
public class ModbusTestSlave extends ProtocolSlave {
    private AbstractRegister register;

    public ModbusTestSlave(String name, ProtocolSlaveParams params, DeviceEntity deviceEntity, TagBlankEntity tagBlankEntity) {
        super(name, params, deviceEntity, tagBlankEntity);
        this.register = new ErrRegister("Error");
    }

    @Override
    public synchronized AbstractRegister getData() {
        return this.register;
    }

    @Override
    public void setNoResponse() {
        this.register = new ErrRegister("NR");
    }

    @Override
    public synchronized void request() throws Exception {
        int rand = ThreadLocalRandom.current().nextInt(1600, 2800 + 1);

        this.register = new Int16Div10Register(rand);
    }

    @Override
    public void init(ProtocolSlaveParams params) {

    }
}
