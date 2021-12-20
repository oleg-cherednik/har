package com.epam.har.users;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class Handler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public static final int OK = 200;
    public static final int METHOD_NOT_ALLOWED = 405;
    public static final int SERVER_ERROR = 500;

    private static final String APPLICATION_JSON_UTF8 = "application/json;charset=utf-8";

    private final ObjectMapper objectMapper;
    private final ServiceDecorator serviceDecorator;

    public Handler() {
        objectMapper = new ObjectMapper();
        serviceDecorator = new ServiceDecorator(objectMapper);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        try {
            log.info("ENVIRONMENT VARIABLES: {}", objectMapper.writeValueAsString(System.getenv()));
            log.info("EVENT: {}", objectMapper.writeValueAsString(event));

            APIGatewayProxyResponseEvent res = null;

            if ("POST".equalsIgnoreCase(event.getHttpMethod())) {
                if ("/health/tip".equalsIgnoreCase(event.getResource()))
                    res = getHealthTips(Objects.requireNonNull(event.getQueryStringParameters().get("userId")));
            }

            if (res == null) {
                log.info("ERROR RESPONSE: Not implemented");
                return new APIGatewayProxyResponseEvent().withStatusCode(METHOD_NOT_ALLOWED);
            }

            log.info("RESPONSE: {}", objectMapper.writeValueAsString(res));
            return res;
        } catch (Exception e) {
            log.info("ERROR RESPONSE: {}", e.getMessage(), e);
            return new APIGatewayProxyResponseEvent().withStatusCode(SERVER_ERROR);
        }
    }

    private APIGatewayProxyResponseEvent getHealthTips(String userId) throws IOException {
        List<ResponseDto> response = serviceDecorator.requestHealthTip();
        List<UserMeasurementData> userMeasurementData =
                serviceDecorator.convert(response).stream()
                                .map(d -> {
                                    d.setUserId(userId);
                                    d.setDateTime(String.valueOf(System.currentTimeMillis()));
                                    return d;
                                }).collect(Collectors.toList());

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(OK)
                .withHeaders(Map.of(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_UTF8))
                .withBody(new ObjectMapper().writeValueAsString(List.of(userMeasurementData)));
    }

}
