package com.epam.har.users;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class UserDao {

    private final DynamoDBMapper dynamoMapper;

    @NonNull
    public List<User> findAll() {
        return dynamoMapper.scan(User.class, new DynamoDBScanExpression());
    }

    @NonNull
    public User getById(String userId) {
        return dynamoMapper.load(User.class, userId);
    }

}
