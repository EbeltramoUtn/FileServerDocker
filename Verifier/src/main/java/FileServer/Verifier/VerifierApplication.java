package FileServer.Verifier;

import FileServer.Verifier.Services.VerifierService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VerifierApplication implements CommandLineRunner {
	private final VerifierService verifierService;
	public VerifierApplication(VerifierService verifierService) {
		this.verifierService = verifierService;
	}
	public static void main(String[] args) {
		SpringApplication.run(VerifierApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		verifierService.verifyFiles();
	}
}
