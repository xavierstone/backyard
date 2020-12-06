package com.xavierstone.backyard.security;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class HashHelper {
    // Currently uses the PBKDF2 hash algorithm
    private static String algorithm = "PBKDF2WithHmacSHA1";

    private String password; // password entered by user
    private byte[] salt;
    private byte[] hash;

    public HashHelper(String password){
        this.password = password;

        // Generate salt
        SecureRandom random = new SecureRandom();
        salt = new byte[16];
        random.nextBytes(salt);
    }

    public HashHelper(String password, byte[] salt){
        this.password = password;
        this.salt = salt;
    }

    public byte[] getSalt() {
        return salt;
    }

    // Generate Hash
    public byte[] getHash() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(algorithm);
        hash = factory.generateSecret(spec).getEncoded();
        return hash;
    }

    // Check match against hash
    public boolean checkMatch(byte[] checkHash) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (hash == null)
            hash = getHash();

        boolean match = true;
        for (int i = 0; i < hash.length; i++){
            if (hash[i] != checkHash[i])
                match = false;
        }

        return match;
    }
}
