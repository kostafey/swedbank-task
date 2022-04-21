package com.kostafey.swedbanktest.db;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class FloorDAO {

    public static void saveOrUpdate(Floor floor) {
        Session session = HibernateUtil.getSession();
        try {
            Transaction tx = session.beginTransaction();
            if (floor.getId() == null) {
                session.save(floor);
            } else {
                session.update(floor);
            }
            tx.commit();
        } catch (HibernateException e) {
            e.printStackTrace();
        } finally {
            HibernateUtil.closeSession();
        }
    }

}
