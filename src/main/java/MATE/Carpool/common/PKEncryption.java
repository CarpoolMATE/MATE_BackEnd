package MATE.Carpool.common;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class PKEncryption {
    private final String secretKey;


    public PKEncryption(@Value("${encryption.secret-key}") String secretKey) {
        this.secretKey = secretKey;
    }

    private static final String ALGORITHM = "AES";

    // 암호화
    public  String encrypt(Long pk) throws Exception {
        String pkStr = pk.toString();
        SecretKey key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(pkStr.getBytes(StandardCharsets.UTF_8)); // String을 byte[]로 암호화
        return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedBytes);
    }

    // 복호화
    public String decrypt(String encrypted) throws Exception {
        // URL-safe Base64로 디코딩
        byte[] decodedBytes = Base64.getUrlDecoder().decode(encrypted);
        SecretKey key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] originalBytes = cipher.doFinal(decodedBytes);
        return new String(originalBytes, StandardCharsets.UTF_8);
    }
}

