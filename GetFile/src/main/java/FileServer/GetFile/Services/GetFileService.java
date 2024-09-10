package FileServer.GetFile.Services;

import FileServer.GetFile.Dtos.FileDto;

public interface GetFileService {
    FileDto getFileById(String sUuid);
}
