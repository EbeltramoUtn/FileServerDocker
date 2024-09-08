package FileServer.SaveFile.controller;


import FileServer.SaveFile.Services.SaveFileService;
import FileServer.SaveFile.dtos.FileDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {
    @Autowired
    private SaveFileService  saveFileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> SaveFile(@RequestPart("file") MultipartFile file,
                                           @RequestPart(value = "hashMd5", required = false) String hashMd5,
                                           @RequestPart(value = "hashSha256", required = false) String hashSha256){
        FileDto fileDto = new FileDto();
        fileDto.setFile(file);
        if(hashMd5 != null)
            fileDto.setHashMd5(hashMd5);
        if(hashSha256 != null)
            fileDto.setHashSha256(hashSha256);
        String id = saveFileService.SaveFile(fileDto);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
