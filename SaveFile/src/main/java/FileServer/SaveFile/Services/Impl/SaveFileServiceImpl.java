package FileServer.SaveFile.Services.Impl;

import FileServer.SaveFile.Services.HashService;
import FileServer.SaveFile.Services.SaveFileService;
import FileServer.SaveFile.dtos.FileDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaveFileServiceImpl implements SaveFileService {
    private final HashService hashService;
    @Autowired
    public SaveFileServiceImpl(HashService hashService) {
        this.hashService = hashService;
    }
    @Override
    public String SaveFile(FileDto fileDto) {

        return "";
    }
}
