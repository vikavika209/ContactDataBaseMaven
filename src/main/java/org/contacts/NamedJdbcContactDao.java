package org.contacts;

import org.contacts.model.Contact;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import org.springframework.jdbc.core.RowMapper;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Primary
public class NamedJdbcContactDao implements ContactDao {
    private static final String GET_CONTACT_SQL = "" +
            "SELECT " +
            "ID, " +
            "NAME, " +
            "PHONE_NUMBER, " +
            "EMAIL " +
            "FROM CONTACTS " +
            "WHERE ID = :id";
    private static final String UPDATE_CONTACT_SQL = "" +
            "UPDATE CONTACTS " +
            "SET NAME = :name, " +
            "PHONE_NUMBER = :phone_number, " +
            "EMAIL = :email " +
            "WHERE ID = :id";
    private static final String ADD_CONTACT_SQL = "" +
            "INSERT INTO CONTACTS (NAME, PHONE_NUMBER, EMAIL) " +
            "VALUES (:name, :phone_number, :email)";

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

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public NamedJdbcContactDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Optional<Contact> getContact(long id) {
        try {
            Contact contact = namedParameterJdbcTemplate.queryForObject(GET_CONTACT_SQL,
                    new MapSqlParameterSource("id", id),
                    CONTACT_ROW_MAPPER);
            return Optional.ofNullable(contact);
        } catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public Map<Long, Contact> getAllContacts() {
        List<Contact> contactList = namedParameterJdbcTemplate.query(GET_ALL_CONTACT_SQL, CONTACT_ROW_MAPPER);
        return contactList.stream()
                .collect(Collectors.toMap(Contact::getId, contact -> contact));
    }

    @Override
    public Contact addContact(String name, String phoneNumber, String email) {
        try {

            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("name", name);
            parameters.addValue("phone_number", phoneNumber);
            parameters.addValue("email", email);
            KeyHolder keyHolder = new GeneratedKeyHolder();

            namedParameterJdbcTemplate.update(ADD_CONTACT_SQL, parameters, keyHolder, new String[]{"id"});

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
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("name", contact.getName());
            parameters.addValue("phone_number", contact.getPhoneNumber());
            parameters.addValue("email", param);
            parameters.addValue("id", id);
            namedParameterJdbcTemplate.update(UPDATE_CONTACT_SQL,parameters);
            return contact;
        }

        else if (isPhoneNumber(param)) {
            Contact contact = new Contact(id, contactMap.get(id).getName(), param, contactMap.get(id).getEmail());
            contactMap.put(id, contact);
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("name", contact.getName());
            parameters.addValue("phone_number", param);
            parameters.addValue("email", contact.getEmail());
            parameters.addValue("id", id);
            namedParameterJdbcTemplate.update(UPDATE_CONTACT_SQL,parameters);
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