package api_payments.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;
import com.google.zxing.client.j2se.MatrixToImageWriter;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

@Service
public class QRCodeGenerationService {

    public byte[] generateQRCode(String walletAddress, BigDecimal amount) throws WriterException, IOException {
        String bitcoinURI = String.format("bitcoin:%s?amount=%.8f", walletAddress, amount);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int width = 300;
        int height = 300;

        BitMatrix bitMatrix = qrCodeWriter.encode(bitcoinURI, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return outputStream.toByteArray();

    }
}
