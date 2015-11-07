package ru.entel.datadealer.db.util;


import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import ru.entel.datadealer.db.entity.Device;
import ru.entel.datadealer.db.entity.Properties;
import ru.entel.datadealer.db.entity.Protocol;
import ru.entel.datadealer.db.entity.Values;

import java.util.ArrayList;
import java.util.List;

public class DataHelper {
    private SessionFactory sessionFactory = null;
    private static DataHelper dataHelper;

    private DataHelper() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    public static synchronized DataHelper getInstance() {
        if (dataHelper == null) {
            dataHelper = new DataHelper();
        }
        return dataHelper;
    }

    private synchronized Session getSession() {
        return sessionFactory.openSession();
    }

    public synchronized String getProperty(String value) {
        Session session = getSession();
        String res = "";
        try {
            Properties property = (Properties)session.createCriteria(Properties.class).add(Restrictions.ilike("name", value)).list().get(0);
            res = property.getValue();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (session!= null && session.isOpen()) {
                session.close();
            }
        }
        return res;
    }


    public synchronized List<Protocol> getAllProtocols() {
        Session session = getSession();
        List<Protocol> res = new ArrayList<>(0);
        try {
            //TODO
            //Исправить баг с дупликатами
            Criteria criteria = session.createCriteria(Protocol.class);
            criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
            res = criteria.list();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (session!= null && session.isOpen()) {
                session.close();
            }
        }
        return res;
    }

    public synchronized List<Device> getAllDevices() {
        Session session = getSession();
        List<Device> res = new ArrayList<>(0);
        try {
            res = session.createCriteria(Device.class).list();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (session!= null && session.isOpen()) {
                session.close();
            }
        }
        System.out.println();
        return res;

    }
}
