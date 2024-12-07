package MATE.Carpool.config.securityConfig.jwt;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique=true, nullable=false)
    private String refreshToken;
    @Column(unique=true,nullable = false)
    private String memberId;
    @Column
    private Long expiresAt;
}
