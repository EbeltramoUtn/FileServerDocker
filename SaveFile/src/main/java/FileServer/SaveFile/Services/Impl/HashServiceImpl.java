package FileServer.SaveFile.Services.Impl;

import FileServer.SaveFile.Services.HashService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Service implementation for generating and verifying file hashes.
 * Provides methods to generate and validate MD5 and SHA-256 hashes for files.
 */
@Service
public class HashServiceImpl implements HashService {

    /**
     * Generates the MD5 hash of a given file.
     *
     * @param file the MultipartFile to hash
     * @return the MD5 hash of the file as a String
     * @throws RuntimeException if there is an error reading the file
     */
    @Override
    public String getMd5String(MultipartFile file) {
        try {
            // Generate the MD5 hash of the file
            return DigestUtils.md5Hex(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Error processing file for MD5 hash", e);
        }
    }

    /**
     * Generates the SHA-256 hash of a given file.
     *
     * @param file the MultipartFile to hash
     * @return the SHA-256 hash of the file as a String
     * @throws RuntimeException if there is an error reading the file
     */
    @Override
    public String getSha256String(MultipartFile file) {
        try {
            // Generate the SHA-256 hash of the file
            return DigestUtils.sha256Hex(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Error processing file for SHA-256 hash", e);
        }
    }

    /**
     * Compares the provided MD5 hash with the calculated hash of the given file.
     *
     * @param file the MultipartFile to hash and compare
     * @param hash the MD5 hash to compare against
     * @return true if the hashes match, false otherwise
     * @throws RuntimeException if there is an error reading the file
     */
    @Override
    public Boolean checkMd5Hash(MultipartFile file, String hash) {
        try {
            // Compare the provided hash with the calculated MD5 hash of the file
            String calculatedHash = DigestUtils.md5Hex(file.getBytes());
            return calculatedHash.equals(hash);
        } catch (IOException e) {
            throw new RuntimeException("Error checking MD5 hash", e);
        }
    }

    /**
     * Compares the provided SHA-256 hash with the calculated hash of the given file.
     *
     * @param file the MultipartFile to hash and compare
     * @param hash the SHA-256 hash to compare against
     * @return true if the hashes match, false otherwise
     * @throws RuntimeException if there is an error reading the file
     */
    @Override
    public Boolean checkSha256Hash(MultipartFile file, String hash) {
        try {
            // Compare the provided hash with the calculated SHA-256 hash of the file
            String calculatedHash = DigestUtils.sha256Hex(file.getBytes());
            return calculatedHash.equals(hash);
        } catch (IOException e) {
            throw new RuntimeException("Error checking SHA-256 hash", e);
        }
    }
}
