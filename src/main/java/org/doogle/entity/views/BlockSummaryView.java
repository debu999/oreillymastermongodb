package org.doogle.entity.views;

import io.quarkus.mongodb.panache.common.ProjectionFor;
import org.doogle.entity.BlockEntity;

@ProjectionFor(BlockEntity.class)
public class BlockSummaryView {

  public String _id;
  public double count;
  public double avgIntTrx;
  public double avgGasUsed;

  public double avgDifficulty;
  public double stdDevDifficulty;

}
