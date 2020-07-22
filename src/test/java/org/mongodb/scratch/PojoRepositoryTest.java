package org.mongodb.scratch;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mongodb.scratch.repository.PojoRepository;
import org.mongodb.scratch.model.Address;
import org.mongodb.scratch.model.Person;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PojoRepositoryTest {

    @Test
    void testInsert() {
        PojoRepository pojoRepository = new PojoRepository();
        Person person = Person.builder()
                .firstName("Mark")
                .lastName("Baker")
                .birthday(LocalDate.of(1985,8,15))
                .addresses(List.of(Address.builder()
                        .line1("the street")
                        .city("London")
                        .postCode("SE1")
                        .build()))
                .emails(List.of("markbm@mongodb.com", "mark.baker@github.com"))
                .build();

        pojoRepository.dropCollection();

//        pojoRepository.insertOnePerson(person);

        assertThat(pojoRepository.findById(person.getId())).isEqualTo(person);

    }

}
