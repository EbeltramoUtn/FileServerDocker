package FileServer.GetFile.Repositories;

import FileServer.GetFile.Entities.ArchiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArchiveRepositoryJpa extends JpaRepository<ArchiveEntity, UUID> {
}
