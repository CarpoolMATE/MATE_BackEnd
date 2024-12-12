package MATE.Carpool.common;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Nested
class PKEncryptionTest {

    @Autowired
    private PKEncryption pkEncryption;
    @Test
    public void testPKEncryption() throws Exception {
        System.out.println("테스트");
        for (long i = 0L; i < 100; i++) {
            String encrypt=pkEncryption.encrypt(i);
            if(encrypt.contains("/")){
                System.out.println("/가 포함된암호화값 "+encrypt +" " +i+"번");
            }
            if(Long.parseLong(pkEncryption.decrypt(encrypt)) != i){
                System.out.println("다른값"+i);
            }





        }

    }

}