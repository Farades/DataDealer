package ru.entel.smiu.datadealer.db.util;


import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import ru.entel.smiu.datadealer.db.entity.*;

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


    public synchronized List<Tag> getTagsByCriteria(DeviceEntity deviceEntity, TagBlankEntity tagBlankEntity, Date date, int first, int pageSize) {
        Session session = getSession();
        List<Tag> res = new ArrayList<>(0);
        try {
            Criteria criteria = session.createCriteria(Tag.class, "t").addOrder(Order.desc("id"));
            criteria.add(Restrictions.eq("t.tagBlankEntity.id", tagBlankEntity.getId()));
            criteria.add(Restrictions.eq("t.deviceEntity.id", deviceEntity.getId()));

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

    public synchronized Long getLogsSizeByCriteria(DeviceEntity deviceEntity, TagBlankEntity tagBlankEntity, Date date) {
        Session session = getSession();
        Long res = new Long(0);
        try {
            Criteria criteria = session.createCriteria(Tag.class, "t").addOrder(Order.desc("id"));
            criteria.add(Restrictions.eq("t.tagBlankEntity.id", tagBlankEntity.getId()));
            criteria.add(Restrictions.eq("t.deviceEntity.id", deviceEntity.getId()));

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

    public synchronized List<ProtocolEntity> getAllProtocols() {
        Session session = getSession();
        List<ProtocolEntity> res = new ArrayList<>(0);
        try {
            //TODO
            //Исправить баг с дупликатами
            Criteria criteria = session.createCriteria(ProtocolEntity.class);
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

    public synchronized List<DeviceEntity> getAllDevices() {
        Session session = getSession();
        List<DeviceEntity> res = new ArrayList<>(0);
        try {
            //TODO
            //Исправить баг с дупликатами
            Criteria criteria = session.createCriteria(DeviceEntity.class);
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

    public synchronized void saveAlarm(AlarmEntity alarmEntity) {
        Session session = DataHelper.getInstance().getSession();
        Transaction tx = session.beginTransaction();
        try {
            session.save(alarmEntity);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            session.flush();
            session.clear();
            if (session!= null && session.isOpen()) {
                tx.commit();
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
