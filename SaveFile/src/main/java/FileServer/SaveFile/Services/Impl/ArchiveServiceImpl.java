package FileServer.SaveFile.Services.Impl;

import FileServer.SaveFile.Entities.ArchiveEntity;
import FileServer.SaveFile.Exception.CustomException;
import FileServer.SaveFile.Model.Archive;
import FileServer.SaveFile.Repositories.ArchiveRepositoryJpa;
import FileServer.SaveFile.Services.ArchiveService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ArchiveServiceImpl implements ArchiveService {
    private final ArchiveRepositoryJpa archiveRepositoryJpa;
    private final ModelMapper modelMapper;
    @Autowired
    public ArchiveServiceImpl(ArchiveRepositoryJpa archiveRepositoryJpa, ModelMapper modelMapper) {
        this.archiveRepositoryJpa = archiveRepositoryJpa;
        this.modelMapper = modelMapper;
    }
    @Override
    public Archive SaveFile(Archive archive) {
        //TODO Impement
        throw new CustomException("Not Implemnted", HttpStatus.METHOD_NOT_ALLOWED);
        //return null;
    }

    @Override
    public Archive GetFileByHash(String hash) {
        Archive result = null;
        Optional<ArchiveEntity> archiveOptional = archiveRepositoryJpa.findByHashSha256(hash);
        if (archiveOptional.isPresent()) {
            result = modelMapper.map(archiveOptional.get(), Archive.class);
        }
        return result;
    }
}
