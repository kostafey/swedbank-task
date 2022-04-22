package com.kostafey.swedbanktest.db;

import java.util.List;

import javax.persistence.TypedQuery;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class FloorDAO {

    public static List<Floor> list() {
        List<Floor> floors = null;
        try {
            TypedQuery<Floor> q = HibernateUtil.getSession().createQuery(
                "SELECT f FROM Floor f",
                Floor.class);
            floors = q.getResultList();
        } catch (HibernateException e) {
            e.printStackTrace();
        } finally {
            HibernateUtil.closeSession();
        }
        return floors;
    }

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
