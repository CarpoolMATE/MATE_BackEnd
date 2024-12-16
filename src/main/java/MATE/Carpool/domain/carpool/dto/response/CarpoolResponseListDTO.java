//package MATE.Carpool.domain.carpool.dto.response;
//
//import MATE.Carpool.domain.carpool.entity.CarpoolEntity;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.time.LocalDateTime;
//
//// 카풀 목록 DTO
//@Getter
//@Setter
//@NoArgsConstructor
//public class CarpoolResponseListDTO {
//
//    //드라이버 프로필 이미지, 즉 멤버 프로필 이미지
//    private String driverImg;
//
//    //출발지 주소
//    private String departureCoordinate;
//
//    private LocalDateTime departureDateTime;
//
//    private int capacity;
//
//    private int reservationCount;
//
//    private int cost;
//
//    public CarpoolResponseListDTO(CarpoolEntity carpool){
//        this.driverImg = carpool.getMember().getProfileImage();
//        this.departureCoordinate = carpool.getDepartureCoordinate();
//        this.departureDateTime = carpool.getDepartureDateTime();
//        this.capacity = carpool.getCapacity();
//        this.reservationCount = carpool.getReservationCount();
//        this.cost=carpool.getCost();
//
//    }
//
//}
