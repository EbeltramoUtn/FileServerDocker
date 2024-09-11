package FileServer.GetFile.Services.Impl;

import FileServer.GetFile.Dtos.FileDto;
import FileServer.GetFile.Entities.ArchiveEntity;
import FileServer.GetFile.Exceptions.CustomException;
import FileServer.GetFile.Models.Archive;
import FileServer.GetFile.Services.ArchiveService;
import FileServer.GetFile.Services.GetFileService;
import FileServer.GetFile.Services.HashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
@Service
public class GetFileServiceImpl implements GetFileService {

    private final ArchiveService archiveService;
    private final HashService hashService;
    @Autowired
    public GetFileServiceImpl(ArchiveService archiveService, HashService hashService) {
        this.archiveService = archiveService;
        this.hashService = hashService;
    }

    @Override
    public FileDto getFileById(String sUuid) {
        Archive archive = archiveService.getFileById(sUuid);
        FileDto result = new FileDto();
        Path filePath = Paths.get(archive.getPath());
        if (Files.exists(filePath)) {
            try{
                result.setBytes(Files.readAllBytes(filePath));
                result.setMimeType(Files.probeContentType(filePath));
                result.setFileName(filePath.getFileName().toString());
                result.setHashMd5(hashService.getSha256String(result.getBytes()));
            }catch (Exception e){
                throw new CustomException("Se produjo un error al obtener el archvio",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }


        } else {
            throw new CustomException("File not found", HttpStatus.NOT_FOUND);
        }
        if(!result.getHashMd5().equals(archive.getHashSha256()))
            throw new CustomException("Integrity file conflict", HttpStatus.CONFLICT);
        return result;
    }
}
