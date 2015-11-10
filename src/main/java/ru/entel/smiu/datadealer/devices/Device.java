package ru.entel.smiu.datadealer.devices;

import org.apache.log4j.Logger;
import ru.entel.smiu.datadealer.db.entity.Values;
import ru.entel.smiu.datadealer.engine.Engine;
import ru.entel.smiu.datadealer.protocols.registers.AbstractRegister;
import ru.entel.smiu.datadealer.protocols.registers.ZeroRegister;
import ru.entel.smiu.datadealer.protocols.service.DDPacket;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author Мацепура Артем
 * @version 0.1
 */
public class Device extends AbstractDevice implements Serializable {
    private static final Logger logger = Logger.getLogger(Device.class);

    private Engine engine;

    /**
     * Флаг для остановки отдельного потока опроса
     */
    private volatile boolean interviewRun = true;

    /**
     * Словарь, содержащий исключительные ситуации
     */
    Set<DeviceException> alarms = new HashSet<DeviceException>();

    /**
     * Название устройства
     */
    private String name;

    /**
     * Описание устройства
     */
    private String description;

    /**
     * Тип устройства
     */
    private DevType type;

    /**
     * Коллекция, хранящая актуальные значения параметров по всем каналам для данного устройства
     */
    private Map<String, AbstractRegister> values = new HashMap<String, AbstractRegister>();

    /**
     * Коллекция, хранящая биндинги для каждого конкретного канала
     */
    private Map<String, Map<String, Binding>> channelsBindings = new HashMap<String, Map<String, Binding>>();

    /**
     * Коллекция, хранящая все каналы с которыми работает данное устройство
     */
    private Set<String> channelsId = new HashSet<String>();

    /**
     * Конструктор
     * @param paramsBindings Ассоциативный массив в котором ключом является название параметра,
     *                       а значением является объект класса Binding.
     */
    public Device(String name, String description, DevType type, HashMap<String, Binding> paramsBindings, Set<DeviceException> alarms, Engine engine) throws Exception {
        super(paramsBindings);
        this.name = name;
        this.description = description;
        this.type = type;
        this.alarms = alarms;
        this.engine = engine;
        if ((paramsBindings == null) || (paramsBindings.size() == 0)) {
            throw new Exception("Params bindings incorrect (==null or size == 0)");
        }
        //Разбиение биндингов по каналам. На выходе получается коллекция channelsBindings
        for (Map.Entry<String, Binding> entry : paramsBindings.entrySet()) {
            this.values.put(entry.getKey(), new ZeroRegister());
            if (!channelsBindings.containsKey(entry.getValue().getChannelID())) {
                Map<String, Binding> tempMap = new HashMap<String, Binding>();
                tempMap.put(entry.getKey(), entry.getValue());
                channelsBindings.put(entry.getValue().getChannelID(), tempMap);
            } else {
                Map<String, Binding> tempMap = channelsBindings.get(entry.getValue().getChannelID());
                tempMap.put(entry.getKey(), entry.getValue());
                channelsBindings.put(entry.getValue().getChannelID(), tempMap);
            }
        }
        //Инициализация сета channelsId
        for (Binding binding : paramsBindings.values()) {
            channelsId.add(binding.getChannelID());
        }
    }


    @Override
    public String toString() {
        return this.values.toString();
    }

    public String getName() {
        return name;
    }

    public Set<DeviceException> getAlarms() {
        return alarms;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, AbstractRegister> getValues() {
        return values;
    }

    public DevType getType() {
        return type;
    }

    @Override
    public void run() {
//        interviewRun = true;
//        while (interviewRun) {
//            try {
//                Thread.sleep(500);
//                try {
//                    updateValues();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public synchronized void updateValues() throws Exception {
        for (String channelId : channelsId) {
            DDPacket packet = engine.sendDataByDevID(channelId);
            for (Map.Entry<String, Binding> cbEntrySet : channelsBindings.get(packet.getDevID()).entrySet()) {
                AbstractRegister value = null;
                for (Map.Entry<Integer, AbstractRegister> valuesEntrySet : packet.getRegisters().entrySet()) {
                    if (valuesEntrySet.getKey() == cbEntrySet.getValue().getRegNumb()) {
                        value = valuesEntrySet.getValue();
                        break;
                    }
                }
                if (value != null) {
                    this.values.put(cbEntrySet.getKey(), value);
                } else {
                    throw new Exception("No register for binding: " + cbEntrySet.getValue());
                }
            }
            logger.debug("\"" + this.name + "\" update values: " + this.values);
        }
        saveValuesToDb();

    }

    public synchronized void saveValuesToDb() {
        for (Map.Entry<String, AbstractRegister> entry : values.entrySet()) {
            Values values = new Values(entry.getKey(), entry.getValue().getValue().toString(), this.name);
        }
    }

    public synchronized void stopInterview() {
        this.interviewRun = false;
    }
}