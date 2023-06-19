package org.doogle.mappers;

import java.util.List;
import org.doogle.entity.PaymentEntity;
import org.doogle.model.Payment;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "jakarta")
public abstract class PaymentMapper {

  @ToModel
  @Named("fromPaymentEntity")
  public abstract Payment fromPaymentEntity(PaymentEntity source);

  @ToEntity
  @Named("toPaymentEntity")
  public abstract PaymentEntity toPaymentEntity(Payment source);

  @IterableMapping(qualifiedByName = "fromPaymentEntity")
  public abstract List<Payment> fromPaymentEntities(List<PaymentEntity> source);

  @IterableMapping(qualifiedByName = "toPaymentEntity")
  public abstract List<PaymentEntity> toPaymentEntities(List<Payment> source);
}
