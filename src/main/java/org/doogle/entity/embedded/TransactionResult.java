package org.doogle.entity.embedded;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResult {
  public TransactionExtractorData extractorData;
  public ZonedDateTime timestamp;
  public int sequenceNumber;
  public String error;
}
