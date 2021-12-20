package com.epam.har.users;

import lombok.Data;

@Data
public class UserMeasurementData {

    private String userId;
    private String healthExpertId;
    private String dateTime;
    private String recommendation;

}
