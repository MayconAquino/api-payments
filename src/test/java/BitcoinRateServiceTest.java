
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.http.HttpMethod;
import service.BitcoinRateService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;


public class BitcoinRateServiceTest {

    private BitcoinRateService bitcoinRateService;
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;

    private static final String MOCK_API_URL = "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=brl";

    @BeforeEach
    public void setup() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);

        bitcoinRateService = new BitcoinRateService() {
            @Override
            public double getBitcoinRate() {
                try {
                    String response = restTemplate.getForObject(MOCK_API_URL, String.class);
                    return parseBitcoinRate(response);
                } catch (Exception e) {
                    throw new RuntimeException("Erro ao obter a cotação do Bitcoin", e);
                }
            }
        };
    }

    @Test
    public void testGetBitcoinRate() {
        // Resposta simulada da API CoinGecko
        String mockJson = "{ \"bitcoin\": { \"brl\": 312345.67 } }";

        mockServer.expect(requestTo(MOCK_API_URL))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mockJson, MediaType.APPLICATION_JSON));

        double rate = bitcoinRateService.getBitcoinRate();
        assertEquals(312345.67, rate, 0.01);
    }
}
