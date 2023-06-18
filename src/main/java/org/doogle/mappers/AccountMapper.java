package org.doogle.mappers;

import java.util.List;
import org.doogle.entity.AccountEntity;
import org.doogle.model.AccountModel;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "jakarta")
public abstract class AccountMapper {
    @ToModel
    @Named("fromAccountEntity")
    public abstract AccountModel fromAccountEntity(AccountEntity source);

    @ToEntity
    @Named("toAccountEntity")
    public abstract AccountEntity toAccountEntity(AccountModel source);

    @IterableMapping(qualifiedByName = "fromAccountEntity")
    public abstract List<AccountModel> fromAccountEntities(List<AccountEntity> source);

    @IterableMapping(qualifiedByName = "toAccountEntity")
    public abstract List<AccountEntity> toAccountEntities(List<AccountModel> source);

}
