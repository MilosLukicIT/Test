package com.service.user.mongock;


import com.service.user.model.User;
import com.service.user.model.enums.UserRole;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Objects;

@ChangeUnit(id="user-updateRoleField", order = "005", author = "milos")
@RequiredArgsConstructor
public class UserUpdateRoleField {

    private final MongoTemplate mongoTemplate;


    @Execution
    public void execution(){

        Query query = new Query();
        query.addCriteria(Criteria.where("userRole").is(UserRole.ADMIN));

        Update updateDefinition = new Update().set("userRole", UserRole.REGULAR);

        mongoTemplate.updateMulti(query, updateDefinition , User.class);

    }

    @RollbackExecution
    public void rollback(){

    }
}
