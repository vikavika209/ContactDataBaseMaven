package org.contacts;

import org.contacts.config.ApplicationConfiguration;
import org.contacts.model.Contact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class NamedJdbcContactDaoTest {

    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
    ContactDao contactDao = applicationContext.getBean(ContactDao.class);

    @Test
    void getContact() {
        Optional<Contact> contact = contactDao.getContact(1L);
        assertEquals("Vasiliy", contact.get().getName());
        assertEquals("88888888888", contact.get().getPhoneNumber());
        assertEquals("vasiliev@mail.ru", contact.get().getEmail());
    }

    @Test
    void getAllContacts() {
        Map<Long, Contact> contactMap = contactDao.getAllContacts();
        assertTrue(contactMap.containsKey(1L));
        assertEquals("Vasiliy", contactMap.get(1L).getName());
    }

    @Test
    void addContact() {
        Contact newContact = contactDao.addContact("Artemiy", "89623577410", "golubev111@gmail.com");
        Map<Long, Contact> contactMap = contactDao.getAllContacts();
        Contact contactToSearch = contactMap.values().stream()
                .filter(contact -> contact.getName().equals(newContact.getName())
                && contact.getEmail().equals(newContact.getEmail())
                && contact.getPhoneNumber().equals(newContact.getPhoneNumber()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Contact not found"));
        assertEquals(contactToSearch.getName(), newContact.getName());
        assertNotNull(contactToSearch.getId(), "Contact ID should be generated");
    }

    @Test
    void updateContact() {
        contactDao.updateContact(1L, "88888888888");
        assertEquals("88888888888", contactDao.getContact(1L).get().getPhoneNumber());
    }
}