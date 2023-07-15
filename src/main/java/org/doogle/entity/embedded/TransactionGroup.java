package org.doogle.entity.embedded;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionGroup {

  @JsonProperty("Block")
  List<Block> block;
  List<Info> from;
  List<Info> to;
  List<Info> txHash;
  List<Text> txFee;
  List<Info> value;
}