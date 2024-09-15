package FileServer.Verifier.Services;

public interface HashService {
    String getSha256String(byte[] file);
}
