package FileServer.SaveFile.Services.Impl;

import FileServer.SaveFile.Services.HashService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class HashServiceImpl implements HashService {

    @Override
    public String getMd5String(MultipartFile file) {
        try {
            // Generar el hash MD5 del archivo
            return DigestUtils.md5Hex(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Error processing file for MD5 hash", e);
        }
    }

    @Override
    public String getSha256String(MultipartFile file) {
        try {
            // Generar el hash SHA-256 del archivo
            return DigestUtils.sha256Hex(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Error processing file for SHA-256 hash", e);
        }
    }

    @Override
    public Boolean checkMd5Hash(MultipartFile file, String hash) {
        try {
            // Comparar el hash MD5 generado con el proporcionado
            String calculatedHash = DigestUtils.md5Hex(file.getBytes());
            return calculatedHash.equals(hash);
        } catch (IOException e) {
            throw new RuntimeException("Error checking MD5 hash", e);
        }
    }

    @Override
    public Boolean checkSha256Hash(MultipartFile file, String hash) {
        try {
            // Comparar el hash SHA-256 generado con el proporcionado
            String calculatedHash = DigestUtils.sha256Hex(file.getBytes());
            return calculatedHash.equals(hash);
        } catch (IOException e) {
            throw new RuntimeException("Error checking SHA-256 hash", e);
        }
    }
}
