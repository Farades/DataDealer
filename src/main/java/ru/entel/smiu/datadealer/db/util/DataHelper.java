package ru.entel.smiu.datadealer.db.util;


import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import ru.entel.smiu.datadealer.db.entity.Device;
import ru.entel.smiu.datadealer.db.entity.Protocol;
import ru.entel.smiu.datadealer.db.entity.Tag;
import ru.entel.smiu.datadealer.db.entity.TagBlank;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    public synchronized Session getSession() {
        return sessionFactory.openSession();
    }

//    public synchronized String getProperty(String value) {
//        Session session = getSession();
//        String res = "";
//        try {
//            Properties property = (Properties)session.createCriteria(Properties.class).add(Restrictions.ilike("name", value)).list().get(0);
//            res = property.getValue();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        } finally {
//            if (session!= null && session.isOpen()) {
//                session.close();
//            }
//        }
//        return res;
//    }


    public synchronized List<Tag> getTagsByCriteria(Device device, TagBlank tagBlank, Date date, int first, int pageSize) {
        Session session = getSession();
        List<Tag> res = new ArrayList<>(0);
        try {
            Criteria criteria = session.createCriteria(Tag.class, "t").addOrder(Order.desc("id"));
            criteria.add(Restrictions.eq("t.tagBlank.id", tagBlank.getId()));
            criteria.add(Restrictions.eq("t.device.id", device.getId()));

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            date =  cal.getTime();
            Date tomorrow = new Date(date.getTime() + (1000 * 60 * 60 * 24) - 1);

            criteria.add(Restrictions.between("tagTime", date, tomorrow));

            criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
            criteria.setFirstResult(first);
            criteria.setMaxResults(pageSize);
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

    public synchronized Long getLogsSizeByCriteria(Device device, TagBlank tagBlank, Date date) {
        Session session = getSession();
        Long res = new Long(0);
        try {
            Criteria criteria = session.createCriteria(Tag.class, "t").addOrder(Order.desc("id"));
            criteria.add(Restrictions.eq("t.tagBlank.id", tagBlank.getId()));
            criteria.add(Restrictions.eq("t.device.id", device.getId()));

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            date =  cal.getTime();
            Date tomorrow = new Date(date.getTime() + (1000 * 60 * 60 * 24) - 1);

            criteria.add(Restrictions.between("tagTime", date, tomorrow));

            criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

            res = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
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
            //TODO
            //Исправить баг с дупликатами
            Criteria criteria = session.createCriteria(Device.class);
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

    public synchronized void clearTags() {
        Session session = getSession();
        try {
            session.createSQLQuery("truncate table tag").executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (session!= null && session.isOpen()) {
                session.close();
            }
        }
    }

    public synchronized void saveTag(Tag tag) {
        Session session = getSession();

        try {
            session.beginTransaction();
            session.save(tag);
            session.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (session!= null && session.isOpen()) {
                session.close();
            }
        }
    }
}
