package FileServer.GetFile.Services.Impl;

import FileServer.GetFile.Exceptions.CustomException;
import FileServer.GetFile.Repositories.ArchiveRepositoryJpa;
import FileServer.GetFile.Services.ArchiveService;
import FileServer.GetFile.Models.Archive;
import FileServer.GetFile.Entities.ArchiveEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ArchiveServiceImpl implements ArchiveService {

    private final ArchiveRepositoryJpa archiveRepositoryJpa;
    private final ModelMapper modelMapper;

    /**
     * Constructor to inject dependencies for repository and model mapper.
     *
     * @param archiveRepositoryJpa the repository to manage archive entities
     * @param modelMapper the ModelMapper used for object mapping between Archive and ArchiveEntity
     */
    @Autowired
    public ArchiveServiceImpl(ArchiveRepositoryJpa archiveRepositoryJpa, ModelMapper modelMapper) {
        this.archiveRepositoryJpa = archiveRepositoryJpa;
        this.modelMapper = modelMapper;
    }

    /**
     * Saves an archive object to the database.
     * If the archive already exists in the database based on its SHA-256 hash, it won't be saved again.
     *
     * @param archive the Archive object containing file information to be saved.
     * @return the saved Archive object, or the existing one if it already exists in the database.
     */
    @Override
    public Archive saveFile(Archive archive) {
        // Validate the archive object before proceeding
        if (archive == null || archive.getId() == null) return null;

        // If the archive already exists in the database (by hash), return the existing archive
        if (getFileByHash(archive.getHashSha256()) != null) return archive;

        try {
            // Map the Archive object to ArchiveEntity and save it in the database
            ArchiveEntity archiveEntity = modelMapper.map(archive, ArchiveEntity.class);
            archiveRepositoryJpa.save(archiveEntity);

            // Map the saved ArchiveEntity back to Archive and return it
            return modelMapper.map(archiveEntity, Archive.class);
        } catch (Exception e) {
            // Handle exceptions, could be improved with better error handling/logging
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Retrieves an archive from the database based on its SHA-256 hash.
     *
     * @param hash the SHA-256 hash of the file to retrieve.
     * @return the corresponding Archive object if found, otherwise null.
     */
    @Override
    public Archive getFileByHash(String hash) {
        Archive result = null;
        Optional<ArchiveEntity> archiveOptional = archiveRepositoryJpa.findByHashSha256(hash);

        // If the archive is found in the database, map it to Archive object
        if (archiveOptional.isPresent()) {
            result = modelMapper.map(archiveOptional.get(), Archive.class);
        }

        return result;
    }

    /**
     * Retrieves an archive from the database based on its UUID.
     *
     * @param sUuid the UUID of the file to retrieve.
     * @return the corresponding Archive object if found, otherwise null.
     */
    @Override
    public Archive getFileById(String sUuid) {
        Archive result = null;
        Optional<ArchiveEntity> archiveEntity = archiveRepositoryJpa.findById(UUID.fromString(sUuid));

        // If the archive is found, map it to Archive object
        if (archiveEntity.isPresent()) {
            result = modelMapper.map(archiveEntity.get(), Archive.class);
        }

        return result;
    }
}
