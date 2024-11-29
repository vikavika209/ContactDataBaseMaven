package org.contacts;

import org.contacts.config.ApplicationConfiguration;
import org.contacts.model.Contact;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Optional;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        var applicationContext = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
        var contactDao = applicationContext.getBean(ContactDao.class);

        Optional<Contact> contact = contactDao.getContact(1L);
        System.out.println(contact);



    }
}