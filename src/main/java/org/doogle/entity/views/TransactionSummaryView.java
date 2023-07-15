package org.doogle.entity.views;

import io.quarkus.mongodb.panache.common.ProjectionFor;
import lombok.Getter;
import org.doogle.entity.TransactionEntity;

@ProjectionFor(TransactionEntity.class)
public class TransactionSummaryView {

  public String _id;
  public long count;

  public double average;
  public double stdDevValue;

  public double avgTrxFee;
  public double stdDevTrxFee;

}
