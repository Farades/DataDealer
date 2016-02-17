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

/**
 * Класс DataHelper - синглтон для работы с Hibernate API
 * Необходим для реализации с JPA (Java Persistence API)
 * @author Мацепура Артем
 * @version 0.2
 */
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

    /**
     * @return Сессия для работы с Hibernate API
     */
    public synchronized Session getSession() {
        return sessionFactory.openSession();
    }

    /**
     * Метод, возвращающий коллекцию конкретных тэгов по заданным в параметрах критериям
     * @param deviceEntity Объект, хранящий в себе необходимую информацию по устройсту
     * @param tagBlankEntity Объект, хранящий в себе необходимую информацию по шаблону тэга
     * @param date Дата по которой необходимо сделать выборку тэгов
     * @param first Номер первого элемента
     * @param pageSize Количество элементов, которые необходимо вернуть
     * @return Коллекция (ArrayList) конкретных тэгов, соответствующих заданным параметрам
     */
    public synchronized List<Tag> getTagsByCriteria(DeviceEntity deviceEntity, TagBlankEntity tagBlankEntity, Date date, int first, int pageSize) {
        Session session = getSession();\n" +
                    "        List<Tag> res = new ArrayList<>(0);\n" +
                    "        try {\n" +
                    "            Criteria criteria = session.createCriteria(Tag.class, \"t\").addOrder(Order.desc(\"id\"));\n" +
                    "            criteria.add(Restrictions.eq(\"t.tagBlankEntity.id\", tagBlankEntity.getId()));\n" +
                    "            criteria.add(Restrictions.eq(\"t.deviceEntity.id", deviceEntity.getId()));

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

    /**
     * Метод, возвращающий количество строк в таблице логов
     * @param deviceEntity Объект, хранящий в себе необходимую информацию по устройсту
     * @param tagBlankEntity Объект, хранящий в себе необходимую информацию по шаблону тэга
     * @param date Дата по которой необходимо сделать выборку тэгов
     * @return Количество строк в таблице логов
     */
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

    /**
     * Метод, возвращающий все объекты-сущности, хранящиеся в таблице ProtocolEntity
     * @return Коллекция (ArrayList), хранящая все объекты класса ProtocolEntity
     */
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

    /**
     * Метод, возвращающий все объекты-сущности, хранящиеся в таблице DeviceEntity
     * @return Коллекция (ArrayList), хранящая все объекты класса DeviceEntity
     */
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

    /**
     * Метод, очищающий таблицу тэгов
     */
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

    /**
     * Метод, сохраняющий объект-сущность alarmEntity в базу данных
     * @param alarmEntity Объект класса-сущности AlarmEntity
     */
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

    /**
     * Метод, сохраняющий объект-сущность tag в базу данных
     * @param tag Объект класса-сущности tag
     */
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
