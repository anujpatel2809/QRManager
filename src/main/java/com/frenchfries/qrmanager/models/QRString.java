package com.frenchfries.qrmanager.models;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class QRString {

    @Pattern(regexp = "^[a-zA-Z0-9+/=]+(-)[a-zA-Z0-9+/=]+$",message = "qrString")
    private String qrString;

}
