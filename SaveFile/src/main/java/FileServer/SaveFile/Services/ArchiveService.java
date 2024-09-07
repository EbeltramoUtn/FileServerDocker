package FileServer.SaveFile.Services;

import FileServer.SaveFile.Model.Archive;

public interface ArchiveService {
    Archive SaveFile(Archive archive);
    Archive GetFileByHash(String hash);
}
