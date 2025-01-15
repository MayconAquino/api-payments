package api_payments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import service.BitcoinRateService;

@SpringBootApplication
public class ApiPaymentsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiPaymentsApplication.class, args);

		BitcoinRateService service = new BitcoinRateService();
		String sampleJson = "{ \"bpi\": { \"BRL\": { \"rate_float\": 230000.0 } } }";

		double rate = service.parseBitcoinRate(sampleJson);
		System.out.println("Cotação do Bitcoin: " + rate);
	}

}
