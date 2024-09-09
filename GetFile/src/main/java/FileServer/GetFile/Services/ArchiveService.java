package FileServer.GetFile.Services;

import FileServer.GetFile.Models.Archive;

public interface ArchiveService {
    Archive saveFile(Archive archive);
    Archive getFileByHash(String hash);
    Archive getFileById(String sUuid);
}
