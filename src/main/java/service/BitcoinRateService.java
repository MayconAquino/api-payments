package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;

public class BitcoinRateService {

    private static final String BITCOIN_RATE_API_URL = "https://api.coindesk.com/v1/bpi/currentprice/BRL.json";

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

            // Navega no JSON para obter o valor da cotação
            JsonNode rateNode = rootNode.path("bpi").path("BRL").path("rate_float");
            return rateNode.asDouble();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao analisar a resposta da API", e);
        }
    }
}

