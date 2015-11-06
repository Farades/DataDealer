package ru.entel.datadealer.engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import ru.entel.datadealer.devices.Binding;
import ru.entel.datadealer.devices.DevType;
import ru.entel.datadealer.devices.Device;
import ru.entel.datadealer.db.util.DataHelper;
import ru.entel.datadealer.devices.DeviceException;
import ru.entel.datadealer.msg.MqttService;
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

import java.util.*;

/**
 * Configurator - Слушает MQTT (ветка smiu/DD/updateConfig), хранит конфигурацию в JSON.
 * По запросу парсит JSON конфиг и возвращает коллекцию объектов ProtocolMaster
 * @author Мацепура Артем
 * @version 0.2
 */
public class Configurator implements MqttCallback {
    private static final Logger logger = Logger.getLogger(Configurator.class);

    private Engine engine;

    /**
     * Основная переменная для работы с MQTT
     */
    private MqttClient client;

    /**
     * Ветка, которую слушает MQTT client для обновления конфигов
     */
    private final String CONFIG_TOPIC = "smiu/DD/updateConfig";

    /**
     * Строковый идентификатор данного клиента
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final String CLIENT_ID = "DD-config";

    /**
     * Основный конфиг. Хранится в формате JSON. Получается от MQTT сервера.
     */
    private String jsonConfig = "";

    private String deviceConfig = "";

    public Configurator(Engine engine) {
        this.engine = engine;
        mqttInit();
    }

    /**
     * Основной метод класса. Парсит jsonConfig и создает возвращаемый словарь
     * @return словарь: ключ - название мастера протокола; значение - объект ProtocolMaster, готовый к запуску.
     * @throws InvalidJSONException Невалидный JSON
     * @throws InvalidProtocolTypeException Неизвестный типа протокола
     */
    public synchronized Map<String, ProtocolMaster> getProtocolMasters() throws InvalidJSONException, InvalidProtocolTypeException {
        if (!JSONUtils.isJSONValid(jsonConfig))
            throw new InvalidJSONException("Invalid json");

        Map<String, ProtocolMaster> res = new HashMap<>();

        Gson gson = new GsonBuilder().registerTypeAdapter(Object.class, new JSONNaturalDeserializer()).create();
        Map jsonParams = (Map) gson.fromJson(jsonConfig, Object.class);
        //ArrayList, хранящий информацию обо всех протоколах (если их больше 1)
        ArrayList protocolsParams = (ArrayList) jsonParams.get("protocols");

        //Цикл по всем протоколам
        for (Object protocolParamsObj : protocolsParams) {
            //Словарь, хранящий информацию про конкретный i-ый ProtocolMaster
            Map protocolParams = (Map)protocolParamsObj;
            String protocolType = (String) protocolParams.get("type");

            switch (protocolType) {
                case "MODBUS_MASTER":
                    String masterName = (String) protocolParams.get("name");
                    String portName   = (String) protocolParams.get("portName");
                    String encoding   = (String) protocolParams.get("encoding");
                    String parity     = (String) protocolParams.get("parity");
                    int baudRate      = ((Double)protocolParams.get("baudRate")).intValue();
                    int databits      = ((Double)protocolParams.get("databits")).intValue();
                    int stopbits      = ((Double)protocolParams.get("stopbits")).intValue();
                    int timePause     = ((Double)protocolParams.get("timePause")).intValue();
                    boolean echo      = (Boolean) protocolParams.get("echo");

                    ModbusMasterParams masterParams = new ModbusMasterParams(portName, baudRate, databits, parity,
                            stopbits, encoding, echo, timePause);
                    ModbusMaster master = new ModbusMaster(masterName, masterParams);

                    //Парсинг слейвов для ProtocolMaster'a
                    ArrayList slaves = (ArrayList) protocolParams.get("slaves");
                    for (Object slaveParamsObj : slaves) {
                        //Словарь хранящий информацию про конкретный Slave
                        Map slaveParams = (Map)slaveParamsObj;

                        ModbusFunction mbFunc = ModbusFunction.valueOf(String.valueOf(slaveParams.get("mbFunc")));
                        RegType regType       = RegType.valueOf(String.valueOf(slaveParams.get("mbRegType")));
                        int offset            = ((Double)slaveParams.get("offset")).intValue();
                        int length            = ((Double)slaveParams.get("length")).intValue();
                        int unitID            = ((Double)slaveParams.get("unitId")).intValue();
                        int transDelay        = ((Double)slaveParams.get("transDelay")).intValue();
                        String slaveName      = String.valueOf(slaveParams.get("name"));

                        ModbusSlaveParams sp = new ModbusSlaveParams(unitID, mbFunc, regType, offset,
                                length, transDelay);
                        master.addSlave(new ModbusSlaveRead(slaveName, sp));
                    }
                    res.put(masterName, master);
                    break;
                default:
                    throw new InvalidProtocolTypeException("Invalid protocol type - " + protocolType);
            }
        }
        return res;
    }

    public synchronized Map<String, Device> getDevices() throws InvalidJSONException {
        if (!JSONUtils.isJSONValid(this.deviceConfig))
            throw new InvalidJSONException("Invalid json");


        Gson gson = new GsonBuilder().registerTypeAdapter(Object.class, new JSONNaturalDeserializer()).create();
        Map jsonParams = (Map) gson.fromJson(deviceConfig, Object.class);

        Map<String, Device> res = new HashMap<String, Device>();

        ArrayList jsonDevices = (ArrayList)jsonParams.get("devices");
        for (Object device : jsonDevices) {
            Map deviceParam = (Map)device;
            String devName = String.valueOf(deviceParam.get("name"));
            String devDescr = String.valueOf(deviceParam.get("description"));
            DevType devtype = DevType.valueOf(String.valueOf(deviceParam.get("devType")));
            //Десереализация params binding'ов
            ArrayList jsonBindings = (ArrayList)deviceParam.get("bindings");
            HashMap<String, Binding> bindings = new HashMap<String, Binding>();
            for (Object binding : jsonBindings) {
                Map jsonBinding = (Map)binding;
                String varName = String.valueOf(jsonBinding.get("varName"));
                String protocolMasterName = String.valueOf(jsonBinding.get("protocolMasterName"));
                String channelName = String.valueOf(jsonBinding.get("channelName"));
                int regNumb = ((Double)jsonBinding.get("regNumb")).intValue();
                Binding newBinding = new Binding(protocolMasterName, channelName, regNumb);
                bindings.put(varName, newBinding);
            }
            //Десереализация device exception'ов
            ArrayList jsonExceptions = (ArrayList)deviceParam.get("exceptions");
            Set<DeviceException> alarms = new HashSet<DeviceException>();
            for (Object exception : jsonExceptions) {
                Map jsonException = (Map)exception;
                String varOwnerName = String.valueOf(jsonException.get("varOwnerName"));
                String condition = String.valueOf(jsonException.get("condition"));
                String description = String.valueOf(jsonException.get("description"));
                DeviceException deviceException = new DeviceException(varOwnerName, devDescr, condition, description);
//                if (exceptions.containsKey(varOwnerName)) {
//                    exceptions.get(varOwnerName).add(deviceException);
//                } else {
//                    ArrayList<DeviceException> deviceExceptionArrayList = new ArrayList<DeviceException>();
//                    deviceExceptionArrayList.add(deviceException);
//                    exceptions.put(varOwnerName, deviceExceptionArrayList);
//                }
                alarms.add(deviceException);
            }
            try {
                Device newDevice = new Device(devName, devDescr, devtype, bindings, alarms, engine);
                res.put(devName, newDevice);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        return res;
    }


    /**
     * Инициализация MQTT клиента. Подпись на ветку CONFIG_TOPIC
     */
    private void mqttInit() {
        try {
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);
            client = new MqttClient(MqttService.BROKER_URL, CLIENT_ID, new MemoryPersistence());

            client.setCallback(this);
            client.connect(connectOptions);
            client.subscribe(CONFIG_TOPIC, MqttService.QOS);
        } catch (MqttException e) {
            e.printStackTrace();
            logger.error("Ошибка в функции mqttInit(): " + e.getMessage());
        }
    }


    public void updateConfig() throws InvalidJSONException {
        logger.debug("Configurator update config");
        String jsonConfigTmp = DataHelper.getInstance().getProperty("dd_config");
        String deviceConfigTmp = DataHelper.getInstance().getProperty("device_config");

        if (JSONUtils.isJSONValid(jsonConfigTmp) && JSONUtils.isJSONValid(deviceConfigTmp)) {
            this.jsonConfig = jsonConfigTmp;
            this.deviceConfig = deviceConfigTmp;
        } else {
            throw new InvalidJSONException("Invalid json");
        }

        System.out.println(this.deviceConfig);
        System.out.println(this.jsonConfig);
     }

    /**
     * Перегруженный CallBack-метод интерфейса MqttCallback
     * Вызывается при потери соединения с MQTT сервером
     * @param throwable исключение
     */
    @Override
    public void connectionLost(Throwable throwable) {

    }

    /**
     * Перегруженный CallBack-метод интерфейса MqttCallback
     * Вызывается при получении сообщения в подисанной ветке
     * @param s Название топика
     * @param mqttMessage Сообщение
     * @throws Exception
     */
    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        if (s.equals(CONFIG_TOPIC)) {
            updateConfig();
        }
    }

    /**
     * Перегруженный CallBack-метод интерфейса MqttCallback
     * Вызывается при доставке сообщение на сервер.
     * Используется для асинхронного обмена информацией с сервером.
     * Синхронная доставка используется с помощью
     * MqttDeliveryToken token = topic.publish(message);
     * token.waitForCompletion();
     * @param iMqttDeliveryToken Токен доставки
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        client.disconnect();
    }
}
