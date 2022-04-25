package com.swedbanktest.db;

import java.util.List;
import java.util.Optional;

import javax.persistence.TypedQuery;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class OrderDAO {
    
    public static void saveOrUpdate(Order order) {
        Session session = HibernateUtil.getSession();
        try {
            Transaction tx = session.beginTransaction();
            if (order.getId() == null) {
                session.save(order);
            } else {
                session.update(order);
            }
            tx.commit();
        } catch (HibernateException e) {
            e.printStackTrace();
        } finally {
            HibernateUtil.closeSession();
        }
    }

    public static Optional<Order> get(Long orderId) {
        Session session = HibernateUtil.getSession();
        try {
            return Optional.ofNullable(session.get(Order.class, orderId));
        } catch (HibernateException e) {
            e.printStackTrace();
        } finally {
            HibernateUtil.closeSession();
        }
        return Optional.empty();
    }

    public static List<Order> listActive() {
        List<Order> orders = null;
        try {
            TypedQuery<Order> q = HibernateUtil.getSession().createQuery(
                "SELECT o FROM Order o WHERE o.paid = false",
                Order.class);
            orders = q.getResultList();
        } catch (HibernateException e) {
            e.printStackTrace();
        } finally {
            HibernateUtil.closeSession();
        }
        return orders;
    }    
}
