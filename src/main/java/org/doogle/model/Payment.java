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
public class Payment {

    public String id;
    public long cartIdentifier;
    public long itemIdentifier;
    public long userIdentifier;
    public String status;
}
