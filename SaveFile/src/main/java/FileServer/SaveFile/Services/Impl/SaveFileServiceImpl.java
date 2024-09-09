package FileServer.SaveFile.Services.Impl;

import FileServer.SaveFile.Exception.CustomException;
import FileServer.SaveFile.Messaging.Producer.ArchiveProducer;
import FileServer.SaveFile.Model.Archive;
import FileServer.SaveFile.Services.ArchiveService;
import FileServer.SaveFile.Services.HashService;
import FileServer.SaveFile.Services.SaveFileService;
import FileServer.SaveFile.dtos.FileDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Service implementation for handling file saving logic, including file integrity checks via hash functions (MD5, SHA-256).
 */
@Service
public class SaveFileServiceImpl implements SaveFileService {

    private final HashService hashService;
    private final ArchiveService archiveService;
    private final ArchiveProducer archiveProducer;

    @Value("${app.file-storage-path}")
    private String pathFolder;

    @Autowired
    public SaveFileServiceImpl(HashService hashService, ArchiveService archiveService, ArchiveProducer archiveProducer) {
        this.hashService = hashService;
        this.archiveService = archiveService;
        this.archiveProducer = archiveProducer;
    }

    /**
     * Saves a file after verifying its integrity via MD5 or SHA-256 hashes (if provided).
     * If the file already exists based on the SHA-256 hash, it will not be re-created.
     * Otherwise, a new file is saved, and a message is sent to notify other services.
     *
     * @param fileDto the file transfer object containing the file and optional hashes
     * @return the UUID of the saved or existing file
     * @throws CustomException if the file integrity check fails or an error occurs during saving
     */
    @Override
    public String SaveFile(FileDto fileDto) {
        // If it has MD5 check it
        if(fileDto.getHashMd5() != null && !fileDto.getHashMd5().isEmpty()){
            if (!checkHashMd5(fileDto.getHashMd5(),fileDto.getFile())){
                throw new CustomException("The integrity of the file is compromised (MD5 mismatch)", HttpStatus.CONFLICT);
            }
        }
        // If it has SHA256 check it
        if(fileDto.getHashSha256() != null && !fileDto.getHashSha256().isEmpty()){
            if (!checkHashSha256(fileDto.getHashSha256(),fileDto.getFile())){
                throw new CustomException("The integrity of the file is compromised (SHA-256 mismatch)", HttpStatus.CONFLICT);
            }
        }

        Archive archive;
        // If the file exists, retrieve it, otherwise create a new archive
        archive = archiveService.getFileByHash(
                Objects.requireNonNullElse(fileDto.getHashSha256(), hashService.getSha256String(fileDto.getFile()))
        );

        if(archive == null){
            archive = new Archive();
            generateArchive(fileDto, archive);
            archiveProducer.send(archive);
        }

        return archive.getId().toString();
    }

    /**
     * Generates a new Archive object based on the provided FileDto.
     * Sets the file name, extension, SHA-256 hash, path, creation date, and mime type.
     *
     * @param fileDto the file data transfer object containing the file
     * @param archive the Archive entity to populate
     */
    private void generateArchive(FileDto fileDto, Archive archive) {
        archive.setId(UUID.randomUUID());
        archive.setName(fileDto.getFile().getOriginalFilename());
        archive.setExtension(archive.getName().substring(archive.getName().lastIndexOf(".") + 1));
        archive.setHashSha256(hashService.getSha256String(fileDto.getFile()));

        String filePath = pathFolder + archive.getId().toString() + "." + archive.getExtension();
        archive.setPath(filePath);
        File convertFile = new File(filePath);
        convertFile.getParentFile().mkdirs();
        archive.setCreationDate(LocalDateTime.now());

        // Save the file to the specified path
        try (FileOutputStream fout = new FileOutputStream(convertFile)) {
            fout.write(fileDto.getFile().getBytes());
        } catch (Exception e) {
            throw new CustomException("Error while saving the file", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Set the MIME type of the file
        try {
            archive.setMimeType(Files.probeContentType(Paths.get(filePath)));
        } catch (Exception e) {
            throw new CustomException("Error while saving the file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Verifies if the MD5 hash of the file matches the provided hash.
     *
     * @param hashMd5 the expected MD5 hash
     * @param file    the file to check
     * @return true if the hashes match, false otherwise
     */
    private Boolean checkHashMd5(String hashMd5, MultipartFile file) {
        return hashService.checkMd5Hash(file, hashMd5);
    }

    /**
     * Verifies if the SHA-256 hash of the file matches the provided hash.
     *
     * @param hashSha256 the expected SHA-256 hash
     * @param file       the file to check
     * @return true if the hashes match, false otherwise
     */
    private Boolean checkHashSha256(String hashSha256, MultipartFile file) {
        return hashService.checkSha256Hash(file, hashSha256);
    }
}
