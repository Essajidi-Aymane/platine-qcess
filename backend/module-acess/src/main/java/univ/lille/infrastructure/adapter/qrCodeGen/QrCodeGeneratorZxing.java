package univ.lille.infrastructure.adapter.qrCodeGen;

import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Component;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import lombok.SneakyThrows;
import univ.lille.domain.port.out.QrCodeGenerator;
@Component
public class QrCodeGeneratorZxing implements QrCodeGenerator  {

    @Override
    @SneakyThrows
    public byte[] generatePng(String content, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter(); 
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height); 
        try (ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream()){ 
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return pngOutputStream.toByteArray(); 
            }
        }
    }
    

