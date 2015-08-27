package ru.entel.protocols.service;

/**
 * ProtocolMaster - абстрактный класс, наследуемый от интерфейса Runnable.
 * Является потомком для всех мастеров протоколов. Необходим в качестве полимфорного типа.
 * @author Мацепура Артем
 * @version 0.1
 */
public abstract class ProtocolMaster implements Runnable {
    protected ProtocolType type;
    /**
     * name название конкретного мастера протокола. Например: modbus_in, onewire
     */
    protected String name;

    /**
     * Конструктор. Заставляет всех переопределять метод init, что гарантирует использование параметров.
     * @param name название данного мастера.
     * @param params объект, содержащий в себе необходимые параметры для инициализации мастера.
     */
    public ProtocolMaster(String name, ProtocolMasterParams params) {
        this.name = name;
        init(params);
    }

    @Override
    public void run() {

    }

    public abstract void stop();

    public abstract void addSlave(ProtocolSlave slave);

    public abstract void init(ProtocolMasterParams params);
}
