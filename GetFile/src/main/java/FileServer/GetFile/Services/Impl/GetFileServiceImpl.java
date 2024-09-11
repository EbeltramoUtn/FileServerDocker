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

@Service
public class GetFileServiceImpl implements GetFileService {

    private final ArchiveService archiveService;
    private final HashService hashService;

    /**
     * Constructor to inject the ArchiveService and HashService dependencies.
     *
     * @param archiveService the service responsible for retrieving archive metadata
     * @param hashService the service responsible for generating file hashes
     */
    @Autowired
    public GetFileServiceImpl(ArchiveService archiveService, HashService hashService) {
        this.archiveService = archiveService;
        this.hashService = hashService;
    }

    /**
     * Retrieves the file data by its UUID and performs an integrity check.
     * This method loads the file from the disk and compares its SHA-256 hash with the stored hash.
     * If the file doesn't exist or there is an integrity mismatch, appropriate exceptions are thrown.
     *
     * @param sUuid the unique identifier (UUID) of the file.
     * @return a {@link FileDto} containing the file's data, including its bytes, mime type, and hash.
     * @throws CustomException if the file is not found or if an error occurs while reading the file.
     */
    @Override
    public FileDto getFileById(String sUuid) {
        // Retrieve the archive metadata by UUID
        Archive archive = archiveService.getFileById(sUuid);
        FileDto result = new FileDto();
        Path filePath = Paths.get(archive.getPath());

        // Check if the file exists at the specified path
        if (Files.exists(filePath)) {
            try {
                // Read file data and set the attributes in FileDto
                result.setBytes(Files.readAllBytes(filePath));
                result.setMimeType(Files.probeContentType(filePath));
                result.setFileName(filePath.getFileName().toString());
                result.setHashMd5(hashService.getSha256String(result.getBytes()));
            } catch (Exception e) {
                // Throw a custom exception if an error occurs while reading the file
                throw new CustomException("An error occurred while retrieving the file", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            // Throw a custom exception if the file is not found
            throw new CustomException("File not found", HttpStatus.NOT_FOUND);
        }

        // Compare the computed hash with the stored hash for integrity verification
        if (!result.getHashMd5().equals(archive.getHashSha256())) {
            throw new CustomException("File integrity conflict", HttpStatus.CONFLICT);
        }

        return result;
    }
}
