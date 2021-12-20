package com.epam.har.users;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class Handler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final int OK = 200;
    private static final int NOT_FOUND = 404;
    private static final int METHOD_NOT_ALLOWED = 405;
    private static final int SERVER_ERROR = 500;

    private static final String APPLICATION_JSON_UTF8 = "application/json;charset=utf-8";

    private final UserDao userDao;
    private final ObjectMapper objectMapper;

    public Handler() {
        DynamoDBMapper dynamoMapper = createDynamoMapper();
        userDao = new UserDao(dynamoMapper);
        objectMapper = new ObjectMapper();
    }

    private static DynamoDBMapper createDynamoMapper() {
        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                                                          .withTableNameResolver(EnvPropertyTableNameResolver.INSTANCE)
                                                          .build();
        return new DynamoDBMapper(AmazonDynamoDBClientBuilder.defaultClient(), config);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        try {
            log.info("ENVIRONMENT VARIABLES: {}", objectMapper.writeValueAsString(System.getenv()));
            log.info("EVENT: {}", objectMapper.writeValueAsString(event));

            APIGatewayProxyResponseEvent res = null;

            if ("GET".equalsIgnoreCase(event.getHttpMethod())) {
                if ("/users".equalsIgnoreCase(event.getResource()))
                    res = getAllUsers();
                else if ("/users/{id}".equalsIgnoreCase(event.getResource()))
                    res = getOneUser(Objects.requireNonNull(event.getPathParameters().get("id")));
            }

            if (res == null) {
                log.info("ERROR RESPONSE: Not implemented");
                return new APIGatewayProxyResponseEvent().withStatusCode(METHOD_NOT_ALLOWED);
            }

            log.info("RESPONSE: {}", objectMapper.writeValueAsString(res));
            return res;
        } catch (JsonProcessingException e) {
            log.info("ERROR RESPONSE: {}", e.getMessage(), e);
            return new APIGatewayProxyResponseEvent().withStatusCode(SERVER_ERROR);
        }
    }

    private APIGatewayProxyResponseEvent getAllUsers() throws JsonProcessingException {
        List<User> users = userDao.findAll();

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(OK)
                .withHeaders(Map.of(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_UTF8))
                .withBody(new ObjectMapper().writeValueAsString(users));
    }

    private APIGatewayProxyResponseEvent getOneUser(String userId) throws JsonProcessingException {
        User user = userDao.getById(userId);

        if (user == null)
            return new APIGatewayProxyResponseEvent().withStatusCode(NOT_FOUND);

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(OK)
                .withHeaders(Map.of(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_UTF8))
                .withBody(new ObjectMapper().writeValueAsString(user));
    }

}
