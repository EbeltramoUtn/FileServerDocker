package FileServer.GetFile.Services.Impl;

import FileServer.GetFile.Services.HashService;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;

@Service
public class HashServiceImpl implements HashService {


    @Override
    public String getSha256String(byte[] file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(file);
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }catch (Exception e){
            throw new RuntimeException("Error while hashing file");
        }
    }

    @Override
    public Boolean checkSha256Hash(byte[] file, String hash) {
        String sha256 = getSha256String(file);
        return sha256.equals(hash);
    }
}
