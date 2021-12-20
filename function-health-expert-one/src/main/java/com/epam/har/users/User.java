package com.epam.har.users;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Data;

@Data
@DynamoDBTable(tableName = "env:USER_TABLE_NAME")
public class User {

    @DynamoDBHashKey
    private String id;
    private String firstName;
    private String lastName;

}
