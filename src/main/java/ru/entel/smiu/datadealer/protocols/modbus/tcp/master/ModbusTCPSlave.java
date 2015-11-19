package ru.entel.smiu.datadealer.protocols.modbus.tcp.master;

import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import ru.entel.smiu.datadealer.db.entity.Device;
import ru.entel.smiu.datadealer.db.entity.TagBlank;
import ru.entel.smiu.datadealer.protocols.modbus.ModbusFunction;
import ru.entel.smiu.datadealer.protocols.modbus.exception.ModbusIllegalRegTypeException;
import ru.entel.smiu.datadealer.protocols.modbus.exception.ModbusNoResponseException;
import ru.entel.smiu.datadealer.protocols.registers.*;
import ru.entel.smiu.datadealer.protocols.service.ProtocolSlave;
import ru.entel.smiu.datadealer.protocols.service.ProtocolSlaveParams;

import java.util.Date;

public class ModbusTCPSlave extends ProtocolSlave {
    /**
     * Название Modbus мастера которому принадлежит данный Slave
     */
    private String protocolName;

    /**
     * Объект для TCP коммуникации
     */
    private TCPMasterConnection con;

    /**
     * Номер функции Modbus по которой происходит обращение к Slave устройству
     */
    private ModbusFunction mbFunc;

    /**
     * Тип запрашиваемых регистров (INT16, FLOAT32, BIT)
     */
    private RegType mbRegType;

    /**
     * Номер первого запрашиваемого регистра
     */
    private int offset;

    /**
     * Количество запрашиваемых регистров
     */
    private int length;

    /**
     * Коллекция в которой хранятся последние значения обработанных регистров
     */
    private AbstractRegister register;

    public ModbusTCPSlave(String name, ModbusTCPSlaveParams params, Device device, TagBlank tagBlank) {
        super(name, params, device, tagBlank);
    }

    @Override
    public void init(ProtocolSlaveParams params) {
        if (params instanceof ModbusTCPSlaveParams) {
            ModbusTCPSlaveParams mbParams = (ModbusTCPSlaveParams) params;
            this.mbFunc     = mbParams.getMbFunc();
            this.mbRegType  = mbParams.getMbRegType();
            this.offset     = mbParams.getOffset();
            this.length     = mbParams.getLength();
        } else {
            String msg = "Modbus slave params not instance of ModbusSlaveParams by " + this.protocolName + ":" + this.name;
            throw new IllegalArgumentException(msg);
        }
    }

    public void setProtocolName(String protocolName) {
        this.protocolName = protocolName;
    }

    @Override
    public AbstractRegister getData() {
        return this.register;
    }

    @Override
    public void setNoResponse() {

    }

    @Override
    public void request() throws Exception {
            ModbusRequest req = null;

            switch (mbFunc) {
                case READ_HOLDING_REGS_3: {
                    req = new ReadMultipleRegistersRequest(offset, length);
//                    req = new ReadMultipleRegistersRequest(11, );
                    break;
                }
                default:
                    throw new IllegalArgumentException("Modbus function incorrect by " + this.protocolName + ":" + this.name);
            }

            ModbusTCPTransaction trans = new ModbusTCPTransaction(con);
            trans.setRequest(req);

            switch (mbFunc) {
                case READ_HOLDING_REGS_3: {
                    Date startTime = new Date();
                    trans.execute();
                    System.out.println(new Date().getTime() - startTime.getTime());
                    ReadMultipleRegistersResponse resp = (ReadMultipleRegistersResponse) trans.getResponse();

                    if (resp == null) {
                        throw new ModbusNoResponseException("No response by " + this.protocolName + ":" + this.name
                                + " READ_INPUT_REGS_4 request.");
                    }

                    if (this.mbRegType == RegType.INT16) {
                        for (int n = 0; n < resp.getWordCount(); n++) {
                            Int16Register reg = new Int16Register(this.offset + n, resp.getRegisterValue(n));
//                            registers.put(offset + n, reg);
                            register = reg;
                        }
                    } else if (this.mbRegType == RegType.FLOAT32) {
                        for (int i = 0; i < resp.getWordCount()-1; i+=2) {
                            Float32Register reg = new Float32Register(offset + i, resp.getRegisterValue(i), resp.getRegisterValue(i + 1));
//                            registers.put(this.offset + i, reg);
                            register = reg;
                        }
                    } else if (this.mbRegType == RegType.INT16DIV10) {
                        for (int n = 0; n < resp.getWordCount(); n++) {
                            Int16Div10Register reg = new Int16Div10Register(this.offset + n, resp.getRegisterValue(n));
//                            registers.put(offset + n, reg);
                            register = reg;
                        }
                    } else if (this.mbRegType == RegType.INT16DIV100) {
                        for (int n = 0; n < resp.getWordCount(); n++) {
                            Int16Div100Register reg = new Int16Div100Register(this.offset + n, resp.getRegisterValue(n));
//                            registers.put(offset + n, reg);
                            register = reg;
                        }
                    } else {
                        throw new ModbusIllegalRegTypeException("Illegal reg type for "
                                + this.protocolName + ":" +this.name + " READ_INPUT_REGS_4");
                    }

                    break;
                }
            }
    }

    public void setCon(TCPMasterConnection con) {
        this.con = con;
    }
}
