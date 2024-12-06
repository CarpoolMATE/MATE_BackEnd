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
    // 암호화
    public String encrypt(Long pk) throws Exception {
        String pkStr = pk.toString();
        SecretKey key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(pkStr.getBytes(StandardCharsets.UTF_8)); // String을 byte[]로 암호화
        String encoded = Base64.getEncoder().encodeToString(encryptedBytes); // Base64로 인코딩하여 반환

        // 패딩 제거
        return encoded.replaceAll("=+$", ""); // 끝에 있는 '=' 제거
    }

    // 복호화
    public String decrypt(String encrypted) throws Exception {
        // 패딩 추가
        int paddingCount = 4 - (encrypted.length() % 4);
        if (paddingCount != 4) {
            encrypted = encrypted + "=".repeat(paddingCount); // 복호화 시 필요한 만큼 '=' 추가
        }

        SecretKey key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedBytes = Base64.getDecoder().decode(encrypted);
        byte[] originalBytes = cipher.doFinal(decodedBytes);
        return new String(originalBytes, StandardCharsets.UTF_8);
    }
}

