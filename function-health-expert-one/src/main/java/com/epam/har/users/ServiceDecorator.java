package com.epam.har.users;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class ServiceDecorator {

    private static final String HEALTH_EXPERT_ID = "one";

    private final ObjectMapper objectMapper;

    public List<ResponseDto> requestHealthTip() throws IOException {
        log.debug("Request health tip");

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String hostUrl = Objects.requireNonNull(System.getenv("HOST_URL"));
            HttpPost httpPost = new HttpPost(hostUrl);

            RequestDto request = RequestDto.builder()
                                           .height("184.0")
                                           .weight("84.0")
                                           .token("service1-dev")
                                           .build();

            log.debug("request: {}", objectMapper.writeValueAsString(request));

            httpPost.setEntity(new StringEntity(objectMapper.writeValueAsString(request)));
            CloseableHttpResponse response = client.execute(httpPost);

            if (response.getStatusLine().getStatusCode() >= Handler.SERVER_ERROR) {
                ErrorDto error = objectMapper.readValue(response.getEntity().getContent(), ErrorDto.class);
                log.error("Cannot receive health tip: {}", objectMapper.writeValueAsString(error));
                throw new RuntimeException(error.getErrorMessage());
            }


            List<ResponseDto> res = objectMapper.readValue(response.getEntity().getContent(), new TypeReference<>() {
            });

            log.debug("response: {}", objectMapper.writeValueAsString(res));
            return res;
        }
    }

    public List<UserMeasurementData> convert(List<ResponseDto> res) {
        return res.stream()
                  .map(d -> {
                      UserMeasurementData data = new UserMeasurementData();
                      data.setHealthExpertId(HEALTH_EXPERT_ID);
                      data.setRecommendation(d.getRecommendation());
                      return data;
                  }).collect(Collectors.toList());
    }

}
