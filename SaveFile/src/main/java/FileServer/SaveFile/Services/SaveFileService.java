package FileServer.SaveFile.Services;

import FileServer.SaveFile.dtos.FileDto;
import org.springframework.stereotype.Service;


public interface SaveFileService {
    String SaveFile(FileDto fileDto);
}
