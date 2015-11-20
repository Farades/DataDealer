package ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.tcp.master;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import ru.entel.smiu.datadealer.hardware_engine.Channel;
import ru.entel.smiu.datadealer.hardware_engine.ChannelParams;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.ModbusFunction;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.exception.ModbusIllegalRegTypeException;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.exception.ModbusNoResponseException;
import ru.entel.smiu.datadealer.hardware_engine.protocols.registers.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;

public class ModbusTCPChannel extends Channel {
    /**
     * Объект для TCP коммуникации
     */
    private TCPMasterConnection con;

    private ModbusTCPTransaction trans;
    private ReadMultipleRegistersRequest req;
    private ReadMultipleRegistersResponse res;

    private String ipAddr;

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

    public ModbusTCPChannel(String protocolName, String name, String ipAddr, ModbusTCPChannelParams params) {
        super(protocolName, name, params);
    }


    @Override
    public synchronized void init(ChannelParams params) {
        if (params instanceof ModbusTCPChannelParams) {
            ModbusTCPChannelParams mbParams = (ModbusTCPChannelParams) params;
            this.mbFunc     = mbParams.getMbFunc();
            this.mbRegType  = mbParams.getMbRegType();
            this.offset     = mbParams.getOffset();
            this.length     = mbParams.getLength();
        } else {
            String msg = "Modbus slave params not instance of ModbusChannelParams by " + this.name;
            throw new IllegalArgumentException(msg);
        }
        for (int i = offset; i < offset + length; i++) {
            AbstractRegister register = RegisterFactory.getRegisterByType(mbRegType);
            registers.put(i, register);
        }

        int port = Modbus.DEFAULT_PORT;
        int slaveAddr = 1;
        try {
            InetAddress addr = InetAddress.getByName("192.168.10.189");
            con = new TCPMasterConnection(addr);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        req = new ReadMultipleRegistersRequest(offset, length);
        res = new ReadMultipleRegistersResponse();

        req.setUnitID(slaveAddr);
        res.setUnitID(slaveAddr);

        con.setPort(port);
        try {
            con.connect();
        } catch (Exception e) {
            e.printStackTrace();
            //TODO Looger
        }
        con.setTimeout(2500);

        trans = new ModbusTCPTransaction(con);
        trans.setRetries(5);

        trans.setReconnecting(true);
        trans.setRequest(req);
    }

    @Override
    public synchronized void request() throws Exception {
        Date startTime = new Date();
            switch (mbFunc) {
                case READ_HOLDING_REGS_3: {
                    trans.execute();
                    ReadMultipleRegistersResponse resp = (ReadMultipleRegistersResponse) trans.getResponse();

                    if (resp == null) {
                        throw new ModbusNoResponseException("No response by " + this.name
                                + " request.");
                    }

                    if (this.mbRegType == RegType.INT16) {
                        for (int n = 0; n < resp.getWordCount(); n++) {
                            Int16Register reg = new Int16Register(resp.getRegisterValue(n));
                            registers.put(offset + n, reg);
                        }
                    } else if (this.mbRegType == RegType.FLOAT32) {
                        for (int i = 0; i < resp.getWordCount()-1; i+=2) {
                            Float32Register reg = new Float32Register(resp.getRegisterValue(i), resp.getRegisterValue(i + 1));
                            registers.put(this.offset + i, reg);
                        }
                    } else if (this.mbRegType == RegType.INT16DIV10) {
                        for (int n = 0; n < resp.getWordCount(); n++) {
                            Int16Div10Register reg = new Int16Div10Register(resp.getRegisterValue(n));
                            registers.put(offset + n, reg);
                        }
                    } else if (this.mbRegType == RegType.INT16DIV100) {
                        for (int n = 0; n < resp.getWordCount(); n++) {
                            Int16Div100Register reg = new Int16Div100Register(resp.getRegisterValue(n));
                            registers.put(offset + n, reg);
                        }
                    } else {
                        throw new ModbusIllegalRegTypeException("Illegal reg type for "
                                + this.name + " READ_INPUT_REGS_4");
                    }

                    break;
                }
            }
        long ellapsedTime = new Date().getTime() - startTime.getTime();
        for (Map.Entry<Integer, AbstractRegister> entry : registers.entrySet()) {
            System.out.println("[" + entry.getKey() + "] " + entry.getValue());
        }
        System.out.println("Ellapsed time: " + ellapsedTime);
        System.out.println("----------------------");
    }
}
