package org.doogle.entity.views;

import io.quarkus.mongodb.panache.common.ProjectionFor;
import java.util.Map;
import org.doogle.entity.TransactionEntity;

@ProjectionFor(TransactionEntity.class)
public class SpammerView {

  public long block;
  public String from;
  public String tags;
  public String to;
  public String txHash;
  public long txfee;
  public double value;
  public String reportPeriod;
  public String scamAddress;
  public String scamEmailAddress;

}
