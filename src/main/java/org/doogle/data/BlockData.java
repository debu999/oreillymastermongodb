package org.doogle.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.doogle.entity.embedded.Result;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlockData {

  public String url;
  public Result result;

}
