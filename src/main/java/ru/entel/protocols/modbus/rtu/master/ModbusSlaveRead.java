package ru.entel.protocols.modbus.rtu.master;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.msg.*;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;
import ru.entel.datadealer.db.entity.Device;
import ru.entel.datadealer.db.entity.Tag;
import ru.entel.datadealer.db.entity.TagBlank;
import ru.entel.datadealer.db.util.DataHelper;
import ru.entel.datadealer.msg.*;
import ru.entel.protocols.modbus.ModbusFunction;
import ru.entel.protocols.modbus.exception.ModbusIllegalRegTypeException;
import ru.entel.protocols.modbus.exception.ModbusNoResponseException;
import ru.entel.protocols.modbus.exception.ModbusRequestException;
import ru.entel.protocols.registers.*;
import ru.entel.protocols.service.ProtocolSlave;
import ru.entel.protocols.service.ProtocolSlaveParams;
import ru.entel.utils.RegisterSerializer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс ModbusSlaveRead - потомок ProtocolSlave.
 * Отвечает за составление запросов, обработку и отправку
 * в MessageService полученной информации от Slave устройств
 * @author Мацепура Артем
 * @version 0.2
 */
public class ModbusSlaveRead extends ProtocolSlave {
    private static final Logger logger = Logger.getLogger(ModbusSlaveRead.class);

    /**
     * Объект gson для десериализации и отправки DDPacket по MQTT
     */
    private Gson gson;

    /**
     * Объект messageService предназначен для отправки данных, полученных от Modbus слейвов
     */
    private MessageService messageService = MessageServiceFactory.getMessageService(MessageServiceType.MQTT);

    /**
     * Название топика в который отправляются по MQTT считанные регистры
     */
    private String DATA_TOPIC;

    /**
     * Объект для коммункации с COM-портом.
     */
    private SerialConnection con;

    /**
     * Название Modbus мастера которому принадлежит данный Slave
     */
    private String protocolName;

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
     * Коллекция в которой хранятся последние значения обработанных регистров
     */
    private Map<Integer, AbstractRegister> registers = new HashMap<>();

    /**
     * Конструктор
     * @param name Имя данного слейва
     * @param params Объект класса ModbusSlaveParams, хранящий в себе все необходимые параметры для работы ModbusSlaveRead
     */
    public ModbusSlaveRead(String name, ModbusSlaveParams params, Device device, TagBlank tagBlank) {
        super(name, params, device, tagBlank);
    }

    /**
     * Переопределенный метод родительского класса ProtocolSlave.
     * Необходим для принудительного создания конструктора, принимающего ProtocolSlaveParams.
     * @param params Объект класса ProtocolSlaveParams, содержащий в себе все необходимые параметры для работы ModbusSlaveRead
     */
    @Override
    public void init(ProtocolSlaveParams params) {
        if (params instanceof ModbusSlaveParams) {
            ModbusSlaveParams mbParams = (ModbusSlaveParams) params;
            this.unitId     = mbParams.getUnitId();
            this.mbFunc     = mbParams.getMbFunc();
            this.mbRegType  = mbParams.getMbRegType();
            this.offset     = mbParams.getOffset();
            this.length     = mbParams.getLength();
            this.transDelay = mbParams.getTransDelay();
            this.gson = new GsonBuilder().registerTypeAdapter(AbstractRegister.class, new RegisterSerializer()).create();
        } else {
            String msg = "Modbus slave params not instance of ModbusSlaveParams by " + this.protocolName + ":" + this.name;
            throw new IllegalArgumentException(msg);
        }
    }

    public void setProtocolName(String protocolName) {
        this.protocolName = protocolName;
        setTopicName();
    }

    private void setTopicName() {
        this.DATA_TOPIC = "smiu/DD/" + this.protocolName + ":" + this.name + "/" + "data";
    }

    @Override
    public synchronized Map<Integer, AbstractRegister> getData() {
        return this.registers;
    }

    /**
     * request() - переопределенный метод родительского класса ProtocolSlave.
     * Является основным для ModbusSlaveRead. Этот метод инициирует запрос данных с помощью объекта SerialConnection
     * по протоколу ModBus, обрабатывает полученные в ответ данные и пересылает их в MessageService
     * Метод синхронизирован для потокобезопасности.
     * @throws ModbusIllegalRegTypeException Неверный тип регистров.
     * @throws ModbusRequestException Сбой запроса. Например: неверный адрес, illegal data type, failed to read, ...
     * @throws ModbusNoResponseException Нет ответа от слейва
     */
    @Override
    public synchronized void request() throws ModbusIllegalRegTypeException, ModbusRequestException, ModbusNoResponseException {
        ModbusRequest req;
        logger.trace("\"" + this.protocolName + ":" + this.name + "\" sending request.");

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
                throw new IllegalArgumentException("Modbus function incorrect by " + this.protocolName + ":" + this.name);
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
                            + this.protocolName + ":" +this.name + " READ_COIL_REGS_1");
                }
                try {
                    trans.execute();
                    ReadCoilsResponse resp = (ReadCoilsResponse) trans.getResponse();
                    if (resp == null) {
                        throw new ModbusNoResponseException("No response by " + this.protocolName + ":" + this.name
                                + " READ_COIL_REGS_1 request.");
                    }
                    for (int i = 0; i < this.length; i++) {
                        BitRegister reg = new BitRegister(offset + i, resp.getCoils().getBit(i));
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
                            + this.protocolName + ":" +this.name + " READ_DISCRETE_INPUT_2");
                }
                try {
                    trans.execute();
                    ReadInputDiscretesResponse resp = (ReadInputDiscretesResponse) trans.getResponse();
                    if (resp == null) {
                        throw new ModbusNoResponseException("No response by " + this.protocolName + ":" + this.name
                                + " READ_DISCRETE_INPUT_2 request.");
                    }
                    for (int i = 0; i < this.length; i++) {
                        BitRegister reg = new BitRegister(offset + i, resp.getDiscretes().getBit(i));
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
                        throw new ModbusNoResponseException("No response by " + this.protocolName + ":" + this.name
                                + " READ_HOLDING_REGS_3 request.");
                    }
                    if(tempResp instanceof ExceptionResponse) {
                        //ExceptionResponse data = (ExceptionResponse)tempResp;
                        throw new ModbusRequestException(this.protocolName + ":" + this.name + " ExceptionResponse");
                    } else if(tempResp instanceof ReadMultipleRegistersResponse) {
                        ReadMultipleRegistersResponse resp = (ReadMultipleRegistersResponse)tempResp;
                        Register[] values = resp.getRegisters();

                        if (this.mbRegType == RegType.INT16) {
                            for (int i = 0; i < values.length; i++) {
                                Int16Register reg = new Int16Register(this.offset + i, values[i].getValue());
                                registers.put(this.offset + i, reg);
                            }
                            sendData();
                        } else if (this.mbRegType == RegType.INT16DIV100) {
                            for (int i = 0; i < values.length; i++) {
                                Int16Div100Register reg = new Int16Div100Register(this.offset + i, values[i].getValue());
                                registers.put(this.offset + i, reg);
                            }
                            sendData();
                        } else if(this.mbRegType == RegType.INT16DIV10) {
                            for (int i = 0; i < values.length; i++) {
                                Int16Div10Register reg = new Int16Div10Register(this.offset + i, values[i].getValue());
                                registers.put(this.offset + i, reg);
                            }
                            sendData();
                        } else if (this.mbRegType == RegType.FLOAT32) {
                            for (int i = 0; i < resp.getWordCount() - 1; i+=2) {
                                Float32Register reg = new Float32Register(offset + i, values[i].getValue(), values[i + 1].getValue());
                                registers.put(this.offset + i, reg);
                            }
                            sendData();
                        } else {
                            throw new ModbusIllegalRegTypeException("Illegal reg type for "
                                    + this.protocolName + ":" +this.name + " READ_HOLDING_REGS_3");
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
                        throw new ModbusNoResponseException("No response by " + this.protocolName + ":" + this.name
                                + " READ_INPUT_REGS_4 request.");
                    }
                    if (this.mbRegType == RegType.INT16) {
                        for (int n = 0; n < resp.getWordCount(); n++) {
                            Int16Register reg = new Int16Register(this.offset + n, resp.getRegisterValue(n));
                            registers.put(offset + n, reg);
                        }
                        sendData();
                    } else if (this.mbRegType == RegType.FLOAT32) {
                        for (int i = 0; i < resp.getWordCount()-1; i+=2) {
                            Float32Register reg = new Float32Register(offset + i, resp.getRegisterValue(i), resp.getRegisterValue(i+1));
                            registers.put(this.offset + i, reg);
                        }
                        sendData();
                    } else if (this.mbRegType == RegType.INT16DIV10) {
                        for (int n = 0; n < resp.getWordCount(); n++) {
                            Int16Div10Register reg = new Int16Div10Register(this.offset + n, resp.getRegisterValue(n));
                            registers.put(offset + n, reg);
                        }
                        sendData();
                    } else if (this.mbRegType == RegType.INT16DIV100) {
                        for (int n = 0; n < resp.getWordCount(); n++) {
                            Int16Div100Register reg = new Int16Div100Register(this.offset + n, resp.getRegisterValue(n));
                            registers.put(offset + n, reg);
                        }
                        sendData();
                    } else {
                        throw new ModbusIllegalRegTypeException("Illegal reg type for "
                                + this.protocolName + ":" +this.name + " READ_INPUT_REGS_4");
                    }
                } catch (ModbusException ex) {
                    throw new ModbusRequestException(ex.getMessage());
                }
                break;
            }
        }

        logger.trace("\"" + this.protocolName + ":" + this.name + "\" response registers " + this.registers);
    }

    /**
     * Отправка считанных регистров в MessageService
     */
    private void sendData() {
//        DDPacket packet = new DDPacket(this.protocolName, this.name, this.registers);
//        messageService.send(this.DATA_TOPIC, gson.toJson(packet));

        //TODO
        //Переделать этот кусок кода
        //После рефакторинга значение всегда одно
        //Не имеет смысла хранить словарь registers
        Tag tag = new Tag();
        tag.setTagTime(new Date());
        tag.setDevice(this.device);
        tag.setTagBlank(this.tagBlank);
        for (AbstractRegister register : this.registers.values()) {
            tag.setValue(register.toString());
        }
        DataHelper.getInstance().saveTag(tag);
    }

    public void setCon(SerialConnection con) {
        this.con = con;
    }

    @Override
    public String toString() {
        return this.protocolName + ":" + this.device.getId() +  ":" + this.name;
    }
}
