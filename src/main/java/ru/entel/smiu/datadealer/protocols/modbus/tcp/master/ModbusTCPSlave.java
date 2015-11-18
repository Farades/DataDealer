package ru.entel.smiu.datadealer.protocols.modbus.tcp.master;

import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import ru.entel.smiu.datadealer.db.entity.Device;
import ru.entel.smiu.datadealer.db.entity.TagBlank;
import ru.entel.smiu.datadealer.protocols.modbus.rtu.master.ModbusSlaveParams;
import ru.entel.smiu.datadealer.protocols.registers.AbstractRegister;
import ru.entel.smiu.datadealer.protocols.service.ProtocolSlave;
import ru.entel.smiu.datadealer.protocols.service.ProtocolSlaveParams;

/**
 * Created by farades on 18.11.15.
 */
public class ModbusTCPSlave extends ProtocolSlave {
    /**
     * Название Modbus мастера которому принадлежит данный Slave
     */
    private String protocolName;

    /**
     * Объект для TCP коммуникации
     */
    private TCPMasterConnection con;

    public ModbusTCPSlave(String name, ModbusSlaveParams params, Device device, TagBlank tagBlank) {
        super(name, params, device, tagBlank);
    }

    @Override
    public void init(ProtocolSlaveParams params) {

    }

    public void setProtocolName(String protocolName) {
        this.protocolName = protocolName;
    }

    @Override
    public AbstractRegister getData() {
        return null;
    }

    @Override
    public void setNoResponse() {

    }

    @Override
    public void request() throws Exception {

    }

    public void setCon(TCPMasterConnection con) {
        this.con = con;
    }
}
