package org.contacts;

import org.contacts.model.Contact;
import org.postgresql.replication.PGReplicationConnectionImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import org.springframework.jdbc.core.RowMapper;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.sql.ResultSet;

@Repository
public class JdbcContactService implements ContactService{
    private static final String GET_CONTACT_SQL = "" +
            "SELECT " +
            "ID, " +
            "NAME, " +
            "PHONE_NUMBER, " +
            "EMAIL " +
            "FROM CONTACTS " +
            "WHERE ID = ?";
    private static final String UPDATE_CONTACT_SQL = "" +
            "UPDATE CONTACTS " +
            "SET NAME = ?, " +
            "PHONE_NUMBER = ?, " +
            "EMAIL = ? " +
            "WHERE ID = ?";
    private static final String ADD_CONTACT_SQL = "" +
            "INSERT INTO CONTACTS (NAME, PHONE_NUMBER, EMAIL) " +
            "VALUES (?, ?, ?)";

    private static final String GET_ALL_CONTACT_SQL = "" +
            "SELECT " +
            "ID, " +
            "NAME, " +
            "PHONE_NUMBER, " +
            "EMAIL " +
            "FROM CONTACTS";

    private static final RowMapper<Contact> CONTACT_ROW_MAPPER =
            (rs, rowNum) -> new Contact(
                    (long) rs.getInt("ID"),
                    rs.getString("NAME"),
                    rs.getString("PHONE_NUMBER"),
                    rs.getString("EMAIL"));

    private final JdbcTemplate jdbcTemplate;
    public JdbcContactService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Contact getContact(long id) {
        return null;
    }

    @Override
    public Map<Long, Contact> getAllContacts() {
        return Map.of();
    }

    @Override
    public Contact addContact(String name, String phoneNumber, String email) {
        return null;
    }

    @Override
    public Contact updateContact(long id, String param) {
        return null;
    }

    @Override
    public Optional<Contact> findContact(long id) {
        return Optional.empty();
    }
}
