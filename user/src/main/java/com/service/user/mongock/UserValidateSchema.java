package com.service.user.mongock;

import com.service.user.service.UserService;
import io.mongock.api.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.schema.JsonSchemaProperty;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema;
import org.springframework.data.mongodb.core.validation.Validator;

@ChangeUnit(id="validate-user-schema", order = "001", author = "mongock")
@RequiredArgsConstructor
public class UserValidateSchema {

    private final MongoTemplate mongoTemplate;

    @BeforeExecution
    public void before(){

    }
    @Execution
    public void execution(){

    }

    @RollbackExecution
    public void rollback(){

    }

    @RollbackBeforeExecution
    public void rollbackBefore(){

    }
}
