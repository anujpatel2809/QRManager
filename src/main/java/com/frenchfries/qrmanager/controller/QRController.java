package com.frenchfries.qrmanager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frenchfries.qrmanager.exceptions.ServiceException;
import com.frenchfries.qrmanager.models.QRData;
import com.frenchfries.qrmanager.models.QRString;
import com.frenchfries.qrmanager.models.Response;
import com.frenchfries.qrmanager.services.QRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Base64;

@RestController
@RequestMapping("/qr-manager")
public class QRController {

    private static final ObjectMapper OBJECT_MAPPER=new ObjectMapper();
    private static final Base64.Encoder ENCODER=Base64.getEncoder();
    private static final Base64.Decoder DECODER=Base64.getDecoder();

    private QRService qrService;

    @Autowired
    public QRController(QRService qrService) {
        this.qrService = qrService;
    }

    @CrossOrigin
    @PostMapping(value = "/generate",produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] generateQR(@RequestBody QRData qrData) throws JsonProcessingException, ServiceException {
        String data=OBJECT_MAPPER.writeValueAsString(qrData);

        byte[] signature = qrService.generateSignature(data);

        String encodedData=ENCODER.encodeToString(data.getBytes());
        String encodedSignature=ENCODER.encodeToString(signature);

        byte[] qr = qrService.generateQRCodeImage(encodedData + "-" + encodedSignature, 350, 350);

        return qr;
    }

    @CrossOrigin
    @PostMapping(value = "/verify",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> verifyQR(@RequestBody @Valid QRString qrString) throws ServiceException {
        String[] split = qrString.getQrString().split("-");
        return qrService.verifySignature(new String(DECODER.decode(split[0])), DECODER.decode(split[1]));
    }

}
