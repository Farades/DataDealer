package ru.entel.smiu.datadealer.db.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * HibernateUtil - класс-утилита, хранящий в себе единственный экземпляр класса SessionFactory
 * Объект sessionFactory необходим для создания сессий для работы с Hibernate
 * @author Мацепура Артем
 * @version 0.2
 */
public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static  synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                //creates the session factory from hibernate.cfg.xml
                sessionFactory = new Configuration().configure().buildSessionFactory();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }
}