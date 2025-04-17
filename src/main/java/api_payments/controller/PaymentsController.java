package api_payments.controller;

import api_payments.DTOs.QRCodeResponse;
import api_payments.service.MonitoramentoPagamentoService;
import api_payments.service.PagamentoService;
import com.google.zxing.WriterException;
import api_payments.model.TransacaoPagamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import api_payments.repository.TransacaoPagamentoRepository;
import api_payments.service.BitcoinRateService;
import api_payments.service.QRCodeGenerationService;
import java.math.BigDecimal;
import java.math.RoundingMode;

import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/payment")
public class PaymentsController {

    @Autowired
    private QRCodeGenerationService qrCodeGenerationService;
    @Autowired
    private BitcoinRateService bitcoinRateService;
    @Autowired
    private TransacaoPagamentoRepository repository;
    @Autowired
    private PagamentoService pagamentoService;
    @Autowired
    private MonitoramentoPagamentoService monitoramento;

    @PostMapping(value = "/generate", produces = MediaType.APPLICATION_JSON_VALUE)
    public QRCodeResponse generateQRCode(@RequestParam String walletAddress, @RequestParam double amount) throws IOException, WriterException {
        if (walletAddress == null || walletAddress.isBlank()) {
            throw new IllegalArgumentException("O endereço da wallet é obrigatório.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero.");
        }

        Double bitcoinCotacaoEmReais = bitcoinRateService.getBitcoinRate();

        BigDecimal bitcoinCotacao = BigDecimal.valueOf(bitcoinCotacaoEmReais);

        BigDecimal amountBitcoin = BigDecimal.valueOf(amount).divide(bitcoinCotacao, 8, RoundingMode.HALF_UP);
        System.out.println("amountBitcoin: " + amountBitcoin);

        TransacaoPagamento transacao = pagamentoService.criarTransacao(walletAddress, amountBitcoin);

        byte[] qrCodeBytes = qrCodeGenerationService.generateQRCode(walletAddress, amountBitcoin);

        String qrCodeBase64 = Base64.getEncoder().encodeToString(qrCodeBytes);
        System.out.println("QR CODE EM BYTE: " + qrCodeBase64);

        QRCodeResponse response = new QRCodeResponse();
        response.setQrCodeImage(qrCodeBase64);
        response.setTransactionId(String.valueOf(transacao.getId()));

        return response;
        }

    @GetMapping("/status")
    public ResponseEntity<String> getStatus(@RequestParam String id) {
        return repository.findById(id)
                .map(t -> ResponseEntity.ok("Status da transação: " + t.getStatus()))
                .orElse(ResponseEntity.notFound().build());
        }
    }
