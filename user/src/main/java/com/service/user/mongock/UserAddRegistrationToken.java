package com.service.user.mongock;

import com.service.user.model.User;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Objects;
import java.util.UUID;

@ChangeUnit(id="user-addConfirmationTokenToAll", order = "003", author = "milos")
@RequiredArgsConstructor
public class UserAddRegistrationToken {

    private final MongoTemplate mongoTemplate;


    @Execution
    public void execution(){
        mongoTemplate.findAll(User.class, "users").forEach(user ->
        {
            if(Objects.isNull(user.getConfirmationToken())){
                user.setConfirmationToken(String.valueOf(UUID.randomUUID()));
                mongoTemplate.save(user, "users");
            }
        });

    }

    @RollbackExecution
    public void rollback(){

    }
}
