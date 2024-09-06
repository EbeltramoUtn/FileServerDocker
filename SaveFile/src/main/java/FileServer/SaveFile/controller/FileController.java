package FileServer.SaveFile.controller;


import FileServer.SaveFile.dtos.FileDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> SaveFile(@Valid @ModelAttribute FileDto file){
        return new ResponseEntity<>("", HttpStatus.OK);
    }
}
