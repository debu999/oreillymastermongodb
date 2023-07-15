package org.doogle.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.doogle.entity.embedded.TransactionResult;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionData {

  public String url;
  public TransactionResult result;

}
