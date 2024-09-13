package FileServer.ApiGateway.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/fallback/savefile")
    public ResponseEntity<String> saveFileFallback() {
        return new ResponseEntity<>(
                "El servicio de SaveFile no está disponible. Por favor, intenta más tarde.",
                HttpStatus.SERVICE_UNAVAILABLE
        );
    }

    @GetMapping("/fallback/getfile")
    public ResponseEntity<String> getFileFallback() {
        return new ResponseEntity<>(
                "El servicio de GetFile no está disponible. Por favor, intenta más tarde.",
                HttpStatus.SERVICE_UNAVAILABLE
        );
    }

    // Puedes agregar más métodos de fallback si tienes más servicios
}