package org.doogle.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketModel {

  public String id;
  public String type;
  public Boolean premium;
  public double price;
  public String event;
  public String currency;
}

