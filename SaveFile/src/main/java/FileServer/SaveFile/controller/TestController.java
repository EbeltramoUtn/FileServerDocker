package FileServer.SaveFile.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> testUpload(@RequestPart("file") MultipartFile file) {
        return new ResponseEntity<>("File uploaded successfully", HttpStatus.OK);
    }
}