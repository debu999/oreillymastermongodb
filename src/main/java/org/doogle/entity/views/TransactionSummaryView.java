package org.doogle.entity.views;

import io.quarkus.mongodb.panache.common.ProjectionFor;
import org.doogle.entity.TransactionEntity;

@ProjectionFor(TransactionEntity.class)
public class TransactionSummaryView {

  public String _id;
  public long count;
  public double average;
  public double stdDevValue;


}
