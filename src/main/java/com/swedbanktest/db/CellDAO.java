package com.swedbanktest.db;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class CellDAO {
    
    public static void update(Cell cell) {
        Session session = HibernateUtil.getSession();
        try {
            Transaction tx = session.beginTransaction();            
            session.update(cell);
            tx.commit();
        } catch (HibernateException e) {
            e.printStackTrace();
        } finally {
            HibernateUtil.closeSession();
        }
    }
    
}
