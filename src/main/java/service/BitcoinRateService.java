package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BitcoinRateService {

    private static final String BITCOIN_RATE_API_URL =
            "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=brl";

    public double getBitcoinRate() {
        RestTemplate restTemplate = new RestTemplate();
        try {
            String response = restTemplate.getForObject(BITCOIN_RATE_API_URL, String.class);
            return parseBitcoinRate(response);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter a cotação do Bitcoin", e);
        }
    }

    public double parseBitcoinRate(String jsonResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            return rootNode.path("bitcoin").path("brl").asDouble();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao analisar a resposta da API", e);
        }
    }
}

