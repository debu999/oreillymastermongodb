package org.doogle.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookOrdersModel {

  public String id;
  public String name;
  public int isbn;
  public double price;
}

