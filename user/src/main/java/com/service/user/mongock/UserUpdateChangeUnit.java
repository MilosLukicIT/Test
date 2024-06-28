package com.service.user.mongock;

import com.service.user.model.User;
import io.mongock.api.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;

@ChangeUnit(id="user-updater", order = "002", author = "mongock")
@RequiredArgsConstructor
public class UserUpdateChangeUnit {


    private final MongoTemplate mongoTemplate;


    @Execution
    public void execution(){
        mongoTemplate.findAll(User.class, "users")
                .forEach(user -> {
                    user.setFirstName(user.getFirstName() + "_updated");
                    mongoTemplate.save(user, "users");
                });
    }

    @BeforeExecution
    public void before(){

    }

    @RollbackExecution
    public void rollback(){

    }

    @RollbackBeforeExecution
    public void rollbackBefore(){

    }
}
