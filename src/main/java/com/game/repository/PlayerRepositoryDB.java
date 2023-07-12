package com.game.repository;

import com.game.entity.Player;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import javax.annotation.PreDestroy;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.springframework.stereotype.Repository;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {
    private final String URL = "jdbc:p6spy:mysql://localhost:3306/rpg";
    private final String USERNAME = "root";
    private final String PASSWORD = "mysql";
    private final String SHOW_SQL = "true";
    private final String DIALECT = "org.hibernate.dialect.MySQL8Dialect";
    private final String HBM2DDL_AUTO = "update";
    private final String DRIVER = "com.p6spy.engine.spy.P6SpyDriver";

    private final SessionFactory sessionFactory;
    public PlayerRepositoryDB() {
        Properties properties = new Properties();
        properties.put(Environment.URL, URL);
        properties.put(Environment.USER, USERNAME);
        properties.put(Environment.PASS, PASSWORD);
        properties.put(Environment.DIALECT, DIALECT);
        properties.put(Environment.SHOW_SQL, SHOW_SQL);
        properties.put(Environment.HBM2DDL_AUTO, HBM2DDL_AUTO);
        properties.put(Environment.DRIVER, DRIVER);
        sessionFactory = new Configuration()
            .addAnnotatedClass(Player.class)
            .addProperties(properties)
            .buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        try(Session session = sessionFactory.openSession()) {
            return session.createNativeQuery(
                    "SELECT * FROM rpg.player", Player.class)
                .setFirstResult(pageNumber * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
        }
    }

    @Override
    public int getAllCount() {
        try(Session session = sessionFactory.openSession()) {
            return Math.toIntExact(session.createNamedQuery("getAllCount", Long.class).getSingleResult());
        }
    }

    @Override
    public Player save(Player player) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(player);
            session.getTransaction().commit();
            return player;
        }
    }

    @Override
    public Optional<Player> findById(long id) {
        try(Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(Player.class, id));
        }
    }

    @Override
    public Player update(Player player) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(player);
            session.getTransaction().commit();
            return player;
        }
    }

    @Override
    public void delete(Player player) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(player);
            session.getTransaction().commit();
        }
    }

    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();
    }
}