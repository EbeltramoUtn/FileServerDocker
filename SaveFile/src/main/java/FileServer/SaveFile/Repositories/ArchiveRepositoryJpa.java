package FileServer.SaveFile.Repositories;

import FileServer.SaveFile.Entities.ArchiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface ArchiveRepositoryJpa extends JpaRepository<ArchiveEntity, UUID> {
    Optional<ArchiveEntity> findByHashSha256(String hashSha256);
}
