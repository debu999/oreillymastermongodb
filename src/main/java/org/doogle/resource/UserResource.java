package org.doogle.resource;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import org.doogle.entity.UserEntity;
import org.doogle.mappers.UserMapper;
import org.doogle.model.User;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@GraphQLApi
@ApplicationScoped
public class UserResource {

    @Inject
    UserMapper userMapper;

    @Query("User")
    public Uni<List<User>> getAllUsers() {

        Uni<List<UserEntity>> userEntityUni = UserEntity.listAll();
        return userEntityUni.log().map(u -> userMapper.fromEntities(u)).log();
    }

    @Mutation("addUser")
    public Uni<User> addUser(@Name("user") User user) {

        UserEntity u = userMapper.toEntity(user);
        Log.info(u);
        return Uni.createFrom().item(u).flatMap(UserEntity::persistOrUpdateUser).log("Response").map(obj -> userMapper.fromEntity(obj)).log();
    }
}
