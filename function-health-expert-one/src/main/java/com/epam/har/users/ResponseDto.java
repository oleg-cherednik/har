package com.epam.har.users;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseDto {

    private String confidence;
    private String recommendation;

}
