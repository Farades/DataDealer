package ru.entel.smiu.datadealer.engine;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.entel.smiu.datadealer.db.entity.Tag;
import ru.entel.smiu.datadealer.db.util.DataHelper;
import ru.entel.smiu.datadealer.hardware_engine.protocols.registers.AbstractRegister;
import ru.entel.smiu.datadealer.software_engine.SDevice;
import ru.entel.smiu.datadealer.software_engine.Value;

import java.util.Date;
import java.util.TimerTask;

public class DataSaver extends TimerTask {
    private static final Logger logger = Logger.getLogger(DataSaver.class);

    private Engine engine;

    public DataSaver(Engine engine) {
        this.engine = engine;
    }

    @Override
    public synchronized void run() {
        Date start = new Date();

        Session session = DataHelper.getInstance().getSession();
        Transaction tx = session.beginTransaction();

        int count = 0;
        try {
            for (SDevice device : Engine.getInstance().getSoftwareEngine().getDevices().values()) {
                for (Value value : device.getValues().values()) {
                    AbstractRegister register = value.getRegister();
                    if (register == null) {
                        continue;
                    }

                    count++;

                    Tag tag = new Tag();
                    tag.setTagTime(new Date());
                    tag.setDeviceEntity(device.getDeviceEntity());
                    tag.setTagBlankEntity(value.getTagBlankEntity());
                    tag.setValue(register.toString());

                    session.save(tag);
                    if (count % 20 == 0 ) { //20, same as the JDBC batch size
                        //flush a batch of inserts and release memory:
                        session.flush();
                        session.clear();
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (session!= null && session.isOpen()) {
                tx.commit();
                session.close();
            }
        }

        long ellapsedTime = new Date().getTime() - start.getTime();
        logger.debug("Save all channel to database. Ellapsed Time: " + ellapsedTime + " ms.");
    }

}
