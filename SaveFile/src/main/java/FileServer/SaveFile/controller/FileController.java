package FileServer.SaveFile.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileController {
    @PostMapping("/upload")
    public ResponseEntity<string> SaveFile(@RequestParam("file") MultipartFile file){

    }
}
