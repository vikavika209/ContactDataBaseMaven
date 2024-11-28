package org.contacts;

import org.contacts.model.Contact;
import java.util.Map;
import java.util.Optional;

public interface ContactService {
    Contact getContact(long id);
    Map<Long, Contact> getAllContacts();
    Contact addContact(String name, String phoneNumber, String email);
    Contact updateContact(long id, String param);
    Optional<Contact> findContact(long id);

}