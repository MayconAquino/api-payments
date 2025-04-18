package api_payments.service;

import api_payments.ENUMs.StatusPagamento;
import api_payments.model.TransacaoPagamento;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import api_payments.repository.TransacaoPagamentoRepository;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MonitoramentoPagamentoService {

    @Autowired
    private TransacaoPagamentoRepository repository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Scheduled(fixedDelay = 10000)
    public void verificarPagamentos() {
        List<TransacaoPagamento> pendentes = repository.findByStatus(StatusPagamento.PENDENTE);
        LocalDateTime agora = LocalDateTime.now();

        for (TransacaoPagamento t : pendentes) {
            if (Duration.between(t.getDataCriacao(), agora).toMinutes() < 30) {
                if (verificarNaBlockchain(t.getwalletAddress(), t.getValorBTC())) {
                    t.setStatus(StatusPagamento.PAGO);
                    repository.save(t);
                    System.out.println("Pagamento confirmado para transação: " + t.getId());
                }
            } else {
                t.setStatus(StatusPagamento.EXPIRADO);
                repository.save(t);
                System.out.println("Pagamento expirado para transação: " + t.getId());
            }
        }
    }


    private boolean verificarNaBlockchain(String wallet, BigDecimal valorBTC) {
        try {
            String url = "https://blockstream.info/api/address/" + wallet + "/txs";
            String json = restTemplate.getForObject(url, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            for (JsonNode tx : root) {
                for (JsonNode output : tx.path("vout")) {
                    if (output.path("scriptpubkey_address").asText().equals(wallet)) {
                        double valorRecebido = output.path("value").asDouble() / 100_000_000.0;

                        BigDecimal valorRecebidoBig = BigDecimal
                                .valueOf(valorRecebido)
                                .setScale(8, BigDecimal.ROUND_HALF_UP);

                        BigDecimal margemDeErro = new BigDecimal("0.01");
                        BigDecimal valorMinimo = valorBTC.subtract(margemDeErro);
                        BigDecimal valorMaximo = valorBTC.add(margemDeErro);

                        if (valorRecebidoBig.compareTo(valorMinimo) >= 0 && valorRecebidoBig.compareTo(valorMaximo) <= 0) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao verificar transação: " + e.getMessage());
        }
        return false;
    }
}
