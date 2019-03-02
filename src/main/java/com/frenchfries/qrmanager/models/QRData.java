package com.frenchfries.qrmanager.models;

import lombok.Data;

@Data
public class QRData {

    private String visitorId;
    private String name;
    private String visitorType;
    private String timeIn;
    private String timeOut;

}
