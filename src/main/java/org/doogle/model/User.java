package org.doogle.model;

import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String id;
    private String name;
    private boolean active;
    private int age;
}
