package org.doogle.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountModel {

  public String id;
  public String accountIdentifier;
  public String accountName;
  public double accountBalance;
}

