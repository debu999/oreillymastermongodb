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
public class Group {

  List<Text> height;
  List<Text> blockInformation;
  List<Text> transactions;
  @JsonProperty("Hash")
  List<Text> hash;
  @JsonProperty("ParentHash")
  List<Text> parentHash;
  List<Text> difficulty;
  @JsonProperty("gas_used")
  List<Text> gasUsed;
}