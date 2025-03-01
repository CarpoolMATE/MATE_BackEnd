package MATE.Carpool.domain.member.repository;

import MATE.Carpool.domain.admin.dto.MemberResponseDTO;
import MATE.Carpool.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByMemberId(String memberId);

    @Transactional
    @Modifying
    @Query("UPDATE Member m SET m.reservation = false, m.carpoolId = null WHERE m.reservation = true")
    void updateReservationAndCarpoolId();

    //존재하면 true
    boolean existsByEmail(String email);

    boolean existsByMemberId(String memberId);

    boolean existsByNickname(String nickname);

    Long countByNickname(String nickname);

    @Query("SELECT m FROM Member m WHERE m.memberType = 'STANDARD'")
    Page<MemberResponseDTO> findAllMemberPagination(Pageable pageable);

    @Query("SELECT m FROM Member m WHERE m.isDriver = true")
    Page<MemberResponseDTO> findAllDriverPagination(Pageable pageable);

    @Query("SELECT m FROM Member m WHERE m.memberId LIKE %:keyword%")
    List<Member> findTesterMember(@Param("keyword") String keyword);
}
