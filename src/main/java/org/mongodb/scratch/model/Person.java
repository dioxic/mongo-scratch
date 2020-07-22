package org.mongodb.scratch.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @BsonId
    private ObjectId id;
    private String firstName;
    private String lastName;
    private LocalDate birthday;
    private List<Address> addresses;
    private List<String> emails;

}
