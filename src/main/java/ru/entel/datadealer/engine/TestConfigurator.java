package ru.entel.datadealer.engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.entel.datadealer.db.entity.Device;
import ru.entel.datadealer.db.entity.DeviceBlank;
import ru.entel.datadealer.db.entity.Protocol;
import ru.entel.datadealer.db.entity.TagBlank;
import ru.entel.datadealer.db.util.DataHelper;
import ru.entel.protocols.modbus.ModbusFunction;
import ru.entel.protocols.modbus.rtu.master.ModbusMaster;
import ru.entel.protocols.modbus.rtu.master.ModbusMasterParams;
import ru.entel.protocols.modbus.rtu.master.ModbusSlaveParams;
import ru.entel.protocols.modbus.rtu.master.ModbusSlaveRead;
import ru.entel.protocols.registers.RegType;
import ru.entel.protocols.service.InvalidProtocolTypeException;
import ru.entel.protocols.service.ProtocolMaster;
import ru.entel.utils.InvalidJSONException;
import ru.entel.utils.JSONNaturalDeserializer;
import ru.entel.utils.JSONUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by farades on 06.11.15.
 */
public class TestConfigurator {

    public synchronized Map<String, ProtocolMaster> getProtocolMasters() throws InvalidJSONException, InvalidProtocolTypeException {
        Map<String, ProtocolMaster> res = new HashMap<>();

        List<Protocol> protocols = DataHelper.getInstance().getAllProtocols();
        Gson gson = new GsonBuilder().registerTypeAdapter(Object.class, new JSONNaturalDeserializer()).create();

        for (Protocol protocol : protocols) {
            String jsonConfig = protocol.getProtocolSettings();
            if (!JSONUtils.isJSONValid(jsonConfig))
                throw new InvalidJSONException("Invalid json");

            Map protocolParams = (Map) gson.fromJson(jsonConfig, Object.class);

            switch (protocol.getType()) {
                case "MODBUS_RTU_MASTER":
                    String masterName = protocol.getName();
                    String portName = (String) protocolParams.get("portName");
                    String encoding = "rtu";
                    String parity = (String) protocolParams.get("parity");
                    int baudRate      = ((Double)protocolParams.get("baudRate")).intValue();
                    int databits      = ((Double)protocolParams.get("databits")).intValue();
                    int stopbits      = ((Double)protocolParams.get("stopbits")).intValue();
                    int timePause     = ((Double)protocolParams.get("timePause")).intValue();
                    boolean echo = false;

                    ModbusMasterParams masterParams = new ModbusMasterParams(portName, baudRate, databits, parity,
                            stopbits, encoding, echo, timePause);
                    ModbusMaster master = new ModbusMaster(masterName, masterParams);

                    for (Device device : protocol.getDevices()) {
                        String jsonDevConf = device.getDeviceSettings();
                        if (!JSONUtils.isJSONValid(jsonDevConf))
                            throw new InvalidJSONException("Invalid json");

                        Map slaveParams = (Map) gson.fromJson(jsonDevConf, Object.class);

                        int unitID = ((Double)slaveParams.get("unitId")).intValue();
                        DeviceBlank deviceBlank = device.getDeviceBlank();
                        for (TagBlank tagBlank : deviceBlank.getTagBlanks()) {
                            String tagParams[] = tagBlank.getTagId().split(":");
                            ModbusFunction mbFunc = ModbusFunction.valueOf(String.valueOf(tagParams[0]));
                            RegType regType       = RegType.valueOf(String.valueOf(tagParams[1]));
                            int offset            = Integer.valueOf(tagParams[2]);
                            int length = 1;
                            int transDelay        = tagBlank.getDelay();
                            String slaveName      = tagBlank.getTagName();

                            ModbusSlaveParams sp = new ModbusSlaveParams(unitID, mbFunc, regType, offset,
                                    length, transDelay);
                            master.addSlave(new ModbusSlaveRead(slaveName, sp));
                            System.out.println();
                        }

                        System.out.println(unitID);
                    }
                    res.put(masterName, master);
                    break;

                default:
                    throw new InvalidProtocolTypeException("Invalid protocol type - " + protocol.getType());
            }
        }
        return res;
    }
}
