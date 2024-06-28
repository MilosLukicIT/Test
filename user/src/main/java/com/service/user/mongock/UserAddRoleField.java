package com.service.user.mongock;


import com.service.user.model.User;
import com.service.user.model.enums.UserRole;
import io.mongock.api.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;

import java.util.Objects;

@ChangeUnit(id="user-addRoleField", order = "004", author = "milos")
@RequiredArgsConstructor
public class UserAddRoleField {

    private final MongoTemplate mongoTemplate;


    @Execution
    public void execution(){

        Query query = new Query();
        query.addCriteria(Criteria.where("userRole").is(UserRole.ADMIN));

        Update updateDefinition = new Update().set("userRole", UserRole.REGULAR);


        mongoTemplate.updateMulti(query, updateDefinition ,User.class);


        mongoTemplate.findAll(User.class, "users").forEach(user ->
        {
            if(Objects.isNull(user.getUserRole())){
                user.setUserRole(UserRole.valueOf("ADMIN"));
                mongoTemplate.save(user, "users");
            }
        });

    }

    @RollbackExecution
    public void rollback(){

    }
}
