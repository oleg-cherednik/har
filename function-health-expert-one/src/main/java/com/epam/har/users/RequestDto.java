package com.epam.har.users;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestDto {

    private String height;
    private String weight;
    private String token;

}
