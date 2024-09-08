package FileServer.SaveFile.Services;

import FileServer.SaveFile.Model.Archive;

public interface ArchiveService {
    Archive saveFile(Archive archive);
    Archive getFileByHash(String hash);
}
