package api_payments.controllerTest;

import api_payments.DTOs.QRCodeResponse;
import api_payments.ENUMs.StatusPagamento;
import api_payments.controller.PaymentsController;
import api_payments.model.TransacaoPagamento;
import api_payments.repository.TransacaoPagamentoRepository;
import api_payments.service.BitcoinRateService;
import api_payments.service.MonitoramentoPagamentoService;
import api_payments.service.PagamentoService;
import api_payments.service.QRCodeGenerationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentsControllerTest {

    @InjectMocks
    private PaymentsController paymentsController;

    @Mock
    private QRCodeGenerationService qrCodeGenerationService;

    @Mock
    private BitcoinRateService bitcoinRateService;

    @Mock
    private TransacaoPagamentoRepository repository;

    @Mock
    private PagamentoService pagamentoService;

    @Mock
    private MonitoramentoPagamentoService monitoramento;
    private double amount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateQRCode_Success() throws Exception {
        // Arrange (preparação dos mocks)
        String walletAddress = "1A2B3C4D5E";
        double amount = 100.00;
        double bitcoinRate = 200000.00;
        BigDecimal expectedAmountBitcoin = BigDecimal.valueOf(amount)
                .divide(BigDecimal.valueOf(bitcoinRate), 8, BigDecimal.ROUND_HALF_UP);

        TransacaoPagamento transacao = new TransacaoPagamento();
        transacao.setId(123L);

        byte[] fakeQRCode = "fakeQRCode".getBytes();

        when(bitcoinRateService.getBitcoinRate()).thenReturn(bitcoinRate);
        when(pagamentoService.criarTransacao(walletAddress, expectedAmountBitcoin)).thenReturn(transacao);
        when(qrCodeGenerationService.generateQRCode(walletAddress, expectedAmountBitcoin)).thenReturn(fakeQRCode);

        QRCodeResponse response = paymentsController.generateQRCode(walletAddress, amount);

        assertNotNull(response);
        assertEquals("123", response.getTransactionId()); // "123" em base64 é "MTIz"
        assertEquals(Base64.getEncoder().encodeToString(fakeQRCode), response.getQrCodeImage());

        verify(bitcoinRateService, times(1)).getBitcoinRate();
        verify(pagamentoService, times(1)).criarTransacao(walletAddress, expectedAmountBitcoin);
        verify(qrCodeGenerationService, times(1)).generateQRCode(walletAddress, expectedAmountBitcoin);
    }

    @Test
    void testGenerateQRCode_WalletAddressVazio_DeveLancarExcecao() {
        // Arrange
        String walletAddress = "   ";
        double amount = 100.00;

        // Act + Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentsController.generateQRCode(walletAddress, amount);
        });

        assertEquals("O endereço da wallet é obrigatório.", exception.getMessage());
    }

    @Test
    void testGenerateQRCode_WalletAddressNull_DeveLancarExcecao() {
        String walletAddress = null;
        double amount = 100.00;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentsController.generateQRCode(walletAddress, amount);
        });

        assertEquals("O endereço da wallet é obrigatório.", exception.getMessage());
    }

    @Test
    void testGenerateQRCode_AmountMenorOuIgualAZero_DeveLancarExcecao() {
        String walletAddress = "1BitcoinAddressTest";
        double amount = 0.0;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentsController.generateQRCode(walletAddress, amount);
        });

        assertEquals("O valor deve ser maior que zero.", exception.getMessage());
    }

    @Test
    void testGetStatus_TransacaoEncontrada_DeveRetornarStatusOk() {
        String id = "abc123";
        TransacaoPagamento transacao = new TransacaoPagamento();
        transacao.setStatus(StatusPagamento.PAGO);

        when(repository.findById(id)).thenReturn(Optional.of(transacao));

        ResponseEntity<String> response = paymentsController.getStatus(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Status da transação: PAGO", response.getBody());
    }

    @Test
    void testGetStatus_TransacaoNaoEncontrada_DeveRetornarNotFound() {
        String id = "naoExiste";
        when(repository.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<String> response = paymentsController.getStatus(id);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

}
