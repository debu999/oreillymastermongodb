package org.doogle.entity.views;

import io.quarkus.mongodb.panache.common.ProjectionFor;
import org.doogle.entity.TransactionEntity;

@ProjectionFor(TransactionEntity.class)
public class ActiveTransactionSummaryView {

  public int _id;
  public long count;
  public double transactionValues;

}
