package com.driverapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class EncryptionService {

    @Value("${security.encryption.key}")
    private String encryptionKey;

    @Value("${security.encryption.algorithm}")
    private String algorithm;

    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;

    /**
     * Encrypts sensitive data using AES/GCM/NoPadding
     * @param plainText The text to encrypt
     * @return Base64 encoded encrypted string
     */
    public String encrypt(String plainText) {
        try {
            if (plainText == null || plainText.isEmpty()) {
                return plainText;
            }

            // Generate a random IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            // Create the cipher
            Cipher cipher = Cipher.getInstance(algorithm);
            SecretKeySpec keySpec = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), "AES");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

            // Encrypt the data
            byte[] encryptedData = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // Combine IV and encrypted data
            byte[] combined = new byte[iv.length + encryptedData.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);

            // Return Base64 encoded string
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting data", e);
        }
    }

    /**
     * Decrypts encrypted data using AES/GCM/NoPadding
     * @param encryptedText Base64 encoded encrypted string
     * @return Decrypted plain text
     */
    public String decrypt(String encryptedText) {
        try {
            if (encryptedText == null || encryptedText.isEmpty()) {
                return encryptedText;
            }

            // Decode from Base64
            byte[] combined = Base64.getDecoder().decode(encryptedText);

            // Extract IV and encrypted data
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encryptedData = new byte[combined.length - GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, iv.length);
            System.arraycopy(combined, iv.length, encryptedData, 0, encryptedData.length);

            // Create the cipher
            Cipher cipher = Cipher.getInstance(algorithm);
            SecretKeySpec keySpec = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), "AES");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

            // Decrypt the data
            byte[] decryptedData = cipher.doFinal(encryptedData);
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting data", e);
        }
    }

    /**
     * Encrypts file data
     * @param fileData The file bytes to encrypt
     * @return Encrypted file bytes
     */
    public byte[] encryptFile(byte[] fileData) {
        try {
            if (fileData == null || fileData.length == 0) {
                return fileData;
            }

            // Generate a random IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            // Create the cipher
            Cipher cipher = Cipher.getInstance(algorithm);
            SecretKeySpec keySpec = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), "AES");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

            // Encrypt the data
            byte[] encryptedData = cipher.doFinal(fileData);

            // Combine IV and encrypted data
            byte[] combined = new byte[iv.length + encryptedData.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);

            return combined;
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting file", e);
        }
    }

    /**
     * Decrypts file data
     * @param encryptedFileData The encrypted file bytes
     * @return Decrypted file bytes
     */
    public byte[] decryptFile(byte[] encryptedFileData) {
        try {
            if (encryptedFileData == null || encryptedFileData.length == 0) {
                return encryptedFileData;
            }

            // Extract IV and encrypted data
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encryptedData = new byte[encryptedFileData.length - GCM_IV_LENGTH];
            System.arraycopy(encryptedFileData, 0, iv, 0, iv.length);
            System.arraycopy(encryptedFileData, iv.length, encryptedData, 0, encryptedData.length);

            // Create the cipher
            Cipher cipher = Cipher.getInstance(algorithm);
            SecretKeySpec keySpec = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), "AES");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

            // Decrypt the data
            return cipher.doFinal(encryptedData);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting file", e);
        }
    }

    /**
     * Generates a secure encryption key
     * @return Base64 encoded encryption key
     */
    public static String generateEncryptionKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey secretKey = keyGen.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            throw new RuntimeException("Error generating encryption key", e);
        }
    }
}