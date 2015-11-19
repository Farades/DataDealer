package ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.rtu.master;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.msg.*;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.procimg.Register;
import org.apache.log4j.Logger;
import ru.entel.smiu.datadealer.hardware_engine.Channel;
import ru.entel.smiu.datadealer.hardware_engine.ChannelParams;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.ModbusFunction;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.exception.ModbusIllegalRegTypeException;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.exception.ModbusNoResponseException;
import ru.entel.smiu.datadealer.hardware_engine.protocols.modbus.exception.ModbusRequestException;
import ru.entel.smiu.datadealer.hardware_engine.protocols.registers.*;

/**
 * Класс ModbusChannel - потомок ProtocolSlave.
 * Отвечает за составление запросов, обработку и отправку
 * в MessageService полученной информации от Slave устройств
 * @author Мацепура Артем
 * @version 0.2
 */
public class ModbusChannel extends Channel {
    private static final Logger logger = Logger.getLogger(ModbusChannel.class);

    /**
     * Объект для коммункации с COM-портом.
     */
    private SerialConnection con;

    /**
     * Адрес slave устройства для обращение по Modbus
     */
    private int unitId;

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
     * Таймаут отклика (время ожидания ответа от физического устройства)
     */
    private int transDelay;

    /**
     * Конструктор
     * @param name Имя данного слейва
     * @param params Объект класса ChannelParams, хранящий в себе все необходимые параметры для работы ModbusChannel
     */
    public ModbusChannel(String protocolName, String name, ChannelParams params) {
        super(protocolName, name, params);
    }

    /**
     * Переопределенный метод родительского класса ProtocolSlave.
     * Необходим для принудительного создания конструктора, принимающего ProtocolSlaveParams.
     * @param params Объект класса ProtocolSlaveParams, содержащий в себе все необходимые параметры для работы ModbusChannel
     */
    @Override
    public void init(ChannelParams params) {
        if (params instanceof ModbusChannelParams) {
            ModbusChannelParams mbParams = (ModbusChannelParams) params;
            this.unitId     = mbParams.getUnitId();
            this.mbFunc     = mbParams.getMbFunc();
            this.mbRegType  = mbParams.getMbRegType();
            this.offset     = mbParams.getOffset();
            this.length     = mbParams.getLength();
            this.transDelay = mbParams.getTransDelay();
        } else {
            String msg = "Modbus slave params not instance of ModbusChannelParams by " + ":" + this.name;
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * request() - переопределенный метод родительского класса ProtocolSlave.
     * Является основным для ModbusChannel. Этот метод инициирует запрос данных с помощью объекта SerialConnection
     * по протоколу ModBus, обрабатывает полученные в ответ данные и пересылает их в MessageService
     * Метод синхронизирован для потокобезопасности.
     * @throws ModbusIllegalRegTypeException Неверный тип регистров.
     * @throws ModbusRequestException Сбой запроса. Например: неверный адрес, illegal data type, failed to read, ...
     * @throws ModbusNoResponseException Нет ответа от слейва
     */
    @Override
    public synchronized void request() throws ModbusIllegalRegTypeException, ModbusRequestException, ModbusNoResponseException {
        ModbusRequest req;
        logger.trace("\"" + this.name + "\" sending request.");

        //Создание объекта ModbusRequest в соответствии с кодом функции Modbus
        switch (mbFunc) {
            case READ_COIL_REGS_1: {
                req = new ReadCoilsRequest(offset, length);
                break;
            }
            case READ_DISCRETE_INPUT_2: {
                req = new ReadInputDiscretesRequest(offset, length);
                break;
            }
            case READ_HOLDING_REGS_3: {
                req = new ReadMultipleRegistersRequest(offset, length);
                break;
            }
            case READ_INPUT_REGS_4: {
                req = new ReadInputRegistersRequest(offset, length);
                break;
            }
            default:
                throw new IllegalArgumentException("Modbus function incorrect by " + this.name);
        }

        req.setUnitID(this.unitId);
        req.setHeadless();
        ModbusSerialTransaction trans = new ModbusSerialTransaction(this.con);
        trans.setRequest(req);
        trans.setTransDelayMS(this.transDelay);

        switch (mbFunc) {
            case READ_COIL_REGS_1: {
                if (this.mbRegType != RegType.BIT) {
                    throw new ModbusIllegalRegTypeException("Illegal reg type for "
                            +this.name + " READ_COIL_REGS_1");
                }
                try {
                    trans.execute();
                    ReadCoilsResponse resp = (ReadCoilsResponse) trans.getResponse();
                    if (resp == null) {
                        throw new ModbusNoResponseException("No response by " + this.name
                                + " READ_COIL_REGS_1 request.");
                    }
                    for (int i = 0; i < this.length; i++) {
                        BitRegister reg = new BitRegister(resp.getCoils().getBit(i));
                        registers.put(offset + i, reg);
                    }
                    sendData();
                } catch (ModbusException ex) {
                    throw new ModbusRequestException(ex.getMessage());
                }
                break;
            }
            case READ_DISCRETE_INPUT_2: {
                if (this.mbRegType != RegType.BIT) {
                    throw new ModbusIllegalRegTypeException("Illegal reg type for "
                            + this.name + " READ_DISCRETE_INPUT_2");
                }
                try {
                    trans.execute();
                    ReadInputDiscretesResponse resp = (ReadInputDiscretesResponse) trans.getResponse();
                    if (resp == null) {
                        throw new ModbusNoResponseException("No response by " + this.name
                                + " READ_DISCRETE_INPUT_2 request.");
                    }
                    for (int i = 0; i < this.length; i++) {
                        BitRegister reg = new BitRegister(resp.getDiscretes().getBit(i));
                        registers.put(offset + i, reg);
                    }
                    sendData();
                } catch (ModbusException ex) {
                    throw new ModbusRequestException(ex.getMessage());
                }
                break;
            }
            case READ_HOLDING_REGS_3: {
                try {
                    trans.execute();
                    ModbusResponse tempResp = trans.getResponse();
                    if (tempResp == null) {
                        throw new ModbusNoResponseException("No response by " + this.name
                                + " READ_HOLDING_REGS_3 request.");
                    }
                    if(tempResp instanceof ExceptionResponse) {
                        //ExceptionResponse data = (ExceptionResponse)tempResp;
                        throw new ModbusRequestException(this.name + " ExceptionResponse");
                    } else if(tempResp instanceof ReadMultipleRegistersResponse) {
                        ReadMultipleRegistersResponse resp = (ReadMultipleRegistersResponse)tempResp;
                        Register[] values = resp.getRegisters();

                        if (this.mbRegType == RegType.INT16) {
                            for (int i = 0; i < values.length; i++) {
                                Int16Register reg = new Int16Register(values[i].getValue());
                                registers.put(this.offset + i, reg);
                            }
                            sendData();
                        } else if (this.mbRegType == RegType.INT16DIV100) {
                            for (int i = 0; i < values.length; i++) {
                                Int16Div100Register reg = new Int16Div100Register(values[i].getValue());
                                registers.put(this.offset + i, reg);
                            }
                            sendData();
                        } else if(this.mbRegType == RegType.INT16DIV10) {
                            for (int i = 0; i < values.length; i++) {
                                Int16Div10Register reg = new Int16Div10Register(values[i].getValue());
                                registers.put(this.offset + i, reg);
                            }
                            sendData();
                        } else if (this.mbRegType == RegType.FLOAT32) {
                            for (int i = 0; i < resp.getWordCount() - 1; i+=2) {
                                Float32Register reg = new Float32Register(values[i].getValue(), values[i + 1].getValue());
                                registers.put(this.offset + i, reg);
                            }
                            sendData();
                        } else {
                            throw new ModbusIllegalRegTypeException("Illegal reg type for "
                                    + this.name + " READ_HOLDING_REGS_3");
                        }
                    }
                } catch (ModbusException ex) {
                    throw new ModbusRequestException(ex.getMessage());
                }
                break;
            }
            case READ_INPUT_REGS_4: {
                try {
                    trans.execute();
                    ReadInputRegistersResponse resp = (ReadInputRegistersResponse) trans.getResponse();
                    if (resp == null) {
                        throw new ModbusNoResponseException("No response by " + this.name
                                + " READ_INPUT_REGS_4 request.");
                    }
                    if (this.mbRegType == RegType.INT16) {
                        for (int n = 0; n < resp.getWordCount(); n++) {
                            Int16Register reg = new Int16Register(resp.getRegisterValue(n));
                            registers.put(offset + n, reg);
                        }
                        sendData();
                    } else if (this.mbRegType == RegType.FLOAT32) {
                        for (int i = 0; i < resp.getWordCount()-1; i+=2) {
                            Float32Register reg = new Float32Register(resp.getRegisterValue(i), resp.getRegisterValue(i+1));
                            registers.put(this.offset + i, reg);
                        }
                        sendData();
                    } else if (this.mbRegType == RegType.INT16DIV10) {
                        for (int n = 0; n < resp.getWordCount(); n++) {
                            Int16Div10Register reg = new Int16Div10Register(resp.getRegisterValue(n));
                            registers.put(offset + n, reg);
                        }
                        sendData();
                    } else if (this.mbRegType == RegType.INT16DIV100) {
                        for (int n = 0; n < resp.getWordCount(); n++) {
                            Int16Div100Register reg = new Int16Div100Register(resp.getRegisterValue(n));
                            registers.put(offset + n, reg);
                        }
                        sendData();
                    } else {
                        throw new ModbusIllegalRegTypeException("Illegal reg type for "
                                + this.name + " READ_INPUT_REGS_4");
                    }
                } catch (ModbusException ex) {
                    throw new ModbusRequestException(ex.getMessage());
                }
                break;
            }
        }

        logger.trace("\"" + this.name + "\" response register " + this.registers);
    }

    /**
     * Отправка считанных регистров в MessageService
     */
    private void sendData() {

    }

    public void setCon(SerialConnection con) {
        this.con = con;
    }
}
