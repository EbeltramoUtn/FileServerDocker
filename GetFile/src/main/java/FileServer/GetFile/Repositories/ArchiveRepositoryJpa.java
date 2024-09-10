package FileServer.GetFile.Repositories;

import FileServer.GetFile.Entities.ArchiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ArchiveRepositoryJpa extends JpaRepository<ArchiveEntity, UUID> {
    /**
     * Retrieves an ArchiveEntity by its SHA-256 hash.
     *
     * @param hashSha256 the SHA-256 hash of the archive
     * @return an Optional containing the ArchiveEntity if found, otherwise empty
     */
    Optional<ArchiveEntity> findByHashSha256(String hashSha256);
}
