package org.mongodb.scratch.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    enum Type {
        HOME,
        WORK,
        DELIVERY
    }

    private String line1;
    private String line2;
    private String line3;
    private String postCode;
    private String city;
    private String country;
    private Type type;

}
