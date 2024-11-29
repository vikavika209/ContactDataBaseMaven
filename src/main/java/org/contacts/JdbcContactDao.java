package org.contacts;

import org.contacts.model.Contact;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import org.springframework.jdbc.core.RowMapper;

import java.sql.PreparedStatement;
import java.util.*;


@Repository
class JdbcContactDao implements ContactDao {
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

    public JdbcContactDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Contact> getContact(long id) {
        try {
            Contact contact = jdbcTemplate.queryForObject(GET_CONTACT_SQL, new Object[]{id}, CONTACT_ROW_MAPPER);
            return Optional.ofNullable(contact);
        } catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public Map<Long, Contact> getAllContacts() {
        List<Contact> contactList = jdbcTemplate.query(GET_ALL_CONTACT_SQL, CONTACT_ROW_MAPPER);
        Map<Long, Contact> contactMap = new HashMap<>();
        for (Contact contact : contactList) {
            contactMap.put((long) contact.getId(), contact);
        }
        return contactMap;
    }

    @Override
    public Contact addContact(String name, String phoneNumber, String email) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(ADD_CONTACT_SQL, new String[]{"ID"});
                ps.setString(1, name);
                ps.setString(2, phoneNumber);
                ps.setString(3, email);
                return ps;}, keyHolder);

            var contactId = Objects.requireNonNull(keyHolder.getKey(), "Generated key is null").longValue();
            return new Contact(contactId, name, phoneNumber, email);

        } catch (DataAccessException e) {
            System.err.println("Error adding contact: " + e.getMessage());
            throw new RuntimeException("Failed to add contact", e);
        }
    }

    @Override
    public Contact updateContact(long id, String param) {
        Map<Long, Contact> contactMap = getAllContacts();

        if (isEmail(param)) {
            Contact contact = new Contact(id, contactMap.get(id).getName(), contactMap.get(id).getPhoneNumber(), param);
            contactMap.put(id, contact);
            jdbcTemplate.update(UPDATE_CONTACT_SQL,contact.getName(), contact.getPhoneNumber(), param, contact.getId());
            return contact;
        }

        else if (isPhoneNumber(param)) {
            Contact contact = new Contact(id, contactMap.get(id).getName(), param, contactMap.get(id).getEmail());
            contactMap.put(id, contact);
            jdbcTemplate.update(UPDATE_CONTACT_SQL,contact.getName(), param, contact.getEmail(), contact.getId());
            return contact;
        }
        else throw new IllegalArgumentException("The input string can't be read " + param);

    }

    private boolean isPhoneNumber(String line){
        String phoneNumberRegex = "^\\+?[0-9\\-]{7,15}$";
        return line.matches(phoneNumberRegex);
    }

    private boolean isEmail(String line) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9+.-]+$";
        return line.matches(emailRegex);
    }
}