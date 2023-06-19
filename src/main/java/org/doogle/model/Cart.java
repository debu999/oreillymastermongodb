package org.doogle.model;

import java.util.List;
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
public class Cart {

    public String id;
    public long userIdentifier;
    public long cartIdentifier;
    private List<String> products;
}
