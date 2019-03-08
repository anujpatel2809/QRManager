package com.frenchfries.qrmanager.models;

import lombok.Data;

@Data
public class QRData {

    private String uniqueNum;
    private String visitorName;
    private String visitorIdType;
    private String visitorId;
    private String visitorType;
    private String fromDate;
    private String toDate;
    private String location;
    private String email;
    private String phone;
    private String employeeID;
    private String visitStatus;
    private String appointmentRequired;


}
