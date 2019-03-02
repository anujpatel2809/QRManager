package com.frenchfries.qrmanager.services;

import com.frenchfries.qrmanager.exceptions.ServiceException;
import com.frenchfries.qrmanager.models.Response;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Service
@Slf4j
public class QRService {

    public byte[] generateSignature(String data) throws ServiceException {
        try {

            byte[] privateKeyEncoded = Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("keys/privatekey.key").toURI()));

            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyEncoded);
            KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");

            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

            // Get an instance of Signature object and initialize it.
            Signature signature = Signature.getInstance("SHA1withDSA", "SUN");
            signature.initSign(privateKey);

            // Supply the data to be signed to the Signature object
            // using the update() method and generate the digital
            // signature.
            signature.update(data.getBytes());
            byte[] digitalSignature = signature.sign();
            return digitalSignature;

        } catch (Exception e) {
            log.error("Exception in service: {}", e);
            throw new ServiceException("Unable to sign QR code");
        }
    }

    public byte[] generateQRCodeImage(String text, int width, int height) throws ServiceException {
        byte[] pngData = null;

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

//        Path path = FileSystems.getDefault().getPath(filePath);
//        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

            ByteArrayOutputStream pngOutput = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutput);

            pngData = pngOutput.toByteArray();

        } catch (WriterException | IOException e) {
            log.error("Exception in service: {}", e);
            throw new ServiceException("Unable to generate QR Code");
        }
        return pngData;
    }

    public ResponseEntity<Response> verifySignature(String data, byte[] digitalSignature) throws ServiceException {
        ResponseEntity<Response> responseResponseEntity=null;
        try {
            byte[] publicKeyEncoded = Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("keys/publickey.key").toURI()));

            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyEncoded);
            KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");

            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
            Signature signature = Signature.getInstance("SHA1withDSA", "SUN");
            signature.initVerify(publicKey);
            signature.update(data.getBytes());

            boolean verified = signature.verify(digitalSignature);
            if(verified)
                responseResponseEntity=new ResponseEntity<>(new Response("true"),null,HttpStatus.OK);
            else
                responseResponseEntity=new ResponseEntity<>(new Response("false"),null,HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            log.error("Exception in service: {}", e);
            throw new ServiceException("Unable to verify QR Code");
        }
        return responseResponseEntity;
    }

}
