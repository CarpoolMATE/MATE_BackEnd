package MATE.Carpool.config.jwt;


import jakarta.persistence.*;
import lombok.*;

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
