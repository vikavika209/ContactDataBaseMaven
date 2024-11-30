package org.contacts;

import org.contacts.model.Contact;
import java.util.Map;
import java.util.Optional;

public interface ContactDao {
    Optional<Contact> getContact(long id);
    Map<Long, Contact> getAllContacts();
    Contact addContact(String name, String phoneNumber, String email);
    Contact updateContact(long id, String param);
    boolean deleteContact(long id);
}