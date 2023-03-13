package org.doogle.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntityBase;
import io.smallrye.mutiny.Uni;
import lombok.*;
import org.bson.types.ObjectId;

import javax.persistence.Id;

@EqualsAndHashCode(callSuper = true)
@MongoEntity(collection = "oreilly_user")
@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity extends ReactivePanacheMongoEntityBase {

    @Id
    public ObjectId id;
    public String name;
    public boolean active;
    public int age;

    public static Uni<UserEntity> persistOrUpdateUser(UserEntity user)
    {
        return persistOrUpdate(user).replaceWith(user).log("USERENTITYPERSIST");
    }
}