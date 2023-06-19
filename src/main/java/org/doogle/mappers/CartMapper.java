package org.doogle.mappers;

import java.util.List;
import org.doogle.entity.CartEntity;
import org.doogle.model.Cart;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "jakarta")
public abstract class CartMapper {

  @ToModel
  @Named("fromCartEntity")
  public abstract Cart fromCartEntity(CartEntity source);

  @ToEntity
  @Named("toCartEntity")
  public abstract CartEntity toCartEntity(Cart source);

  @IterableMapping(qualifiedByName = "fromCartEntity")
  public abstract List<Cart> fromCartEntities(List<CartEntity> source);

  @IterableMapping(qualifiedByName = "toCartEntity")
  public abstract List<CartEntity> toCartEntities(List<Cart> source);
}
