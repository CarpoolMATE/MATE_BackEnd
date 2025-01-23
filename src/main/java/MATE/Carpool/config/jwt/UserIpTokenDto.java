package MATE.Carpool.config.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.parameters.P;

@Data
@NoArgsConstructor
public class UserIpTokenDto {

    private String ip;
    private String token;

    public UserIpTokenDto(String ip, String token){
        this.ip = ip;
        this.token = token;
    }



}
