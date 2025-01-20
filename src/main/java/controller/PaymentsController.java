package controller;

import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import service.BitcoinRateService;
import service.QRCodeGenerationService;

import java.io.IOException;

@RestController
@RequestMapping("/payment")
public class PaymentsController {

    @Autowired
    private QRCodeGenerationService qrCodeGenerationService;
    @Autowired
    private BitcoinRateService bitcoinRateService;


    @PostMapping("/generate")
    public byte[] generateQRCode(@RequestParam String walletAddress, @RequestParam double amount) throws IOException, WriterException {
        if (walletAddress == null || walletAddress.isBlank()) {
            throw new IllegalArgumentException("O endereço da wallet é obrigatório.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero.");
        }

        Double bitcoinCotacao = bitcoinRateService.getBitcoinRate();
        Double amountBitcoin = amount / bitcoinCotacao;

        return qrCodeGenerationService.generateQRCode(walletAddress, amountBitcoin);
        }
    }
