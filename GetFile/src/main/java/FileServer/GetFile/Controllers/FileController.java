package FileServer.GetFile.Controllers;

import FileServer.GetFile.Dtos.FileDto;
import FileServer.GetFile.Services.GetFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileController {
    @Autowired
    private GetFileService getFileService;
    @GetMapping("/{uuid}")
    public ResponseEntity<FileDto> getFileDetails(@PathVariable String uuid) {
        FileDto fileDto = getFileService.getFileById(uuid);
        return new ResponseEntity<>(fileDto, HttpStatus.OK);
    }
}
