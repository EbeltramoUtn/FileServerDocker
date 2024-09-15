package FileServer.GetFile.Services;



public interface HashService {
    String getSha256String(byte[] file);
    Boolean checkSha256Hash(byte[] file, String hash);
}