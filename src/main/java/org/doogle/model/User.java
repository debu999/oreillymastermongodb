package org.doogle.model;

import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String id;
    public long userIdentifier;
    private String name;
    private boolean active;
    private int age;
    private String race;
}
