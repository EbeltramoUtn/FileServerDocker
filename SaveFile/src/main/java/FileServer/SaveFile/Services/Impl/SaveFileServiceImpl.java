package FileServer.SaveFile.Services.Impl;

import FileServer.SaveFile.Exception.CustomException;
import FileServer.SaveFile.Model.Archive;
import FileServer.SaveFile.Services.ArchiveService;
import FileServer.SaveFile.Services.HashService;
import FileServer.SaveFile.Services.SaveFileService;
import FileServer.SaveFile.dtos.FileDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
public class SaveFileServiceImpl implements SaveFileService {
    private final HashService hashService;
    private final ArchiveService archiveService;
    private final String PATH_FOLDER = "./fileUpload/";

    @Autowired
    public SaveFileServiceImpl(HashService hashService, ArchiveService archiveService) {
        this.hashService = hashService;
        this.archiveService = archiveService;
    }
    @Override
    public String SaveFile(FileDto fileDto) {
        // If it has MD5 check it
        if(fileDto.getHashMd5() != null && !fileDto.getHashMd5().isEmpty()){
            if (!checkHashMd5(fileDto.getHashMd5(),fileDto.getFile())){
                throw new CustomException("The integrity of the file is compromised (MD5 mismatch)", HttpStatus.CONFLICT);
            }
        }
        //If it has SHA256 check it
        if(fileDto.getHashSha256()!= null && !fileDto.getHashSha256().isEmpty()){
            if (!checkHashSha256(fileDto.getHashSha256(),fileDto.getFile())){
                throw new CustomException("The integrity of the file is compromised (SHA-256 mismatch)", HttpStatus.CONFLICT);
            }
        }
        Archive archive;
        archive = archiveService.GetFileByHash(fileDto.getHashSha256());
        // If the archive exist we don't need created
        if(archive == null){
            generateArchive(fileDto,archive);
            //TODO event RABBITMQ
        }
        return archive.getId().toString();
    }
    private void generateArchive(FileDto fileDto,Archive archive){
        archive = new Archive();
        archive.setId(UUID.randomUUID());
        archive.setName(fileDto.getFile().getOriginalFilename());
        archive.setExtension(archive.getName().substring(archive.getName().lastIndexOf(".") + 1));
        archive.setHashSha256(hashService.getSha256String(fileDto.getFile()));

        String filePath = PATH_FOLDER + archive.getName() + "."+archive.getExtension();
        File convertFile = new File(filePath);
        convertFile.getParentFile().mkdirs();
        archive.setCreationDate(LocalDateTime.now());
        try (FileOutputStream fout = new FileOutputStream(convertFile)) {
            fout.write(fileDto.getFile().getBytes());
        }catch (Exception e){
            throw new CustomException("Error while save the file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        try {
            archive.setMimeType(Files.probeContentType(Paths.get(filePath)));
        }catch (Exception e){
            throw new CustomException("Error while save the file", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    private Boolean checkHashMd5(String hashMd5, MultipartFile file) {
        return hashService.checkMd5Hash(file, hashMd5);
    }
    private Boolean checkHashSha256(String hashSha256, MultipartFile file) {
        return hashService.checkSha256Hash(file, hashSha256);
    }

}
