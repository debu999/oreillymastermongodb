package org.doogle.mappers;

import java.util.List;
import org.doogle.entity.InventoryEntity;
import org.doogle.model.Inventory;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "jakarta")
public abstract class InventoryMapper {

  @ToModel
  @Named("fromInventoryEntity")
  public abstract Inventory fromInventoryEntity(InventoryEntity source);

  @ToEntity
  @Named("toInventoryEntity")
  public abstract InventoryEntity toInventoryEntity(Inventory source);

  @IterableMapping(qualifiedByName = "fromInventoryEntity")
  public abstract List<Inventory> fromInventoryEntities(List<InventoryEntity> source);

  @IterableMapping(qualifiedByName = "toInventoryEntity")
  public abstract List<InventoryEntity> toInventoryEntities(List<Inventory> source);
}
