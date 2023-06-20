package org.doogle.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Inventory {

    public String id;
    public long itemIdentifier;
    public String description;
    public Double price;
    public long quantity;
}
