//package MATE.Carpool.domain.carpool.service;
//
//import MATE.Carpool.domain.carpool.dto.request.CarpoolRequestDTO;
//
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
//import java.util.Random;
//
//public class CarpoolRequestDtoGenerator {
//    private static final Random random = new Random();
//
//    public static LocalDateTime generateRandomDepartureTime() {
//        LocalDateTime now = LocalDateTime.now().plusDays(1);
//        LocalDateTime startOfDay = now.withHour(7).withMinute(0).withSecond(0).withNano(0);
//        LocalDateTime endOfDay = now.withHour(9).withMinute(0).withSecond(0).withNano(0);
//
//        long minutesBetween = ChronoUnit.MINUTES.between(startOfDay, endOfDay);
//        long randomMinutes = random.nextInt((int) (minutesBetween / 10)) * 10L;
//
//        return startOfDay.plusMinutes(randomMinutes);
//    }
//
//    public static String generateRandomCoordinate() {
//        double latitude = 37.0 + (random.nextDouble());
//        double longitude = 126.0 + (random.nextDouble());
//        return String.format("%.6f", latitude) + ", " + String.format("%.6f", longitude);
//    }
//
//    public static String generateRandomChatLink() {
//        return "kakao.com/" + random.nextInt(1000);
//    }
//
//    // 탑승 인원 수 (1명~4명)
//    public static int generateRandomCapacity() {
//        return random.nextInt(4) + 1;
//    }
//
//    // 요금 (1000~5000원, 500단위)
//    public static int generateRandomCost() {
//        return 1000 + (random.nextInt(9) * 500);
//    }
//
//    // CarpoolRequestDTO 생성
//    public static CarpoolRequestDTO generateRandomCarpoolRequestDTO() {
//        CarpoolRequestDTO carpoolRequestDTO = new CarpoolRequestDTO();
//        carpoolRequestDTO.setDepartureCoordinate(generateRandomCoordinate());
//        carpoolRequestDTO.setLatitude(generateRandomCoordinate().split(",")[0]);
//        carpoolRequestDTO.setLongitude(generateRandomCoordinate().split(",")[1]);
//        carpoolRequestDTO.setDepartureTime(generateRandomDepartureTime());
//        carpoolRequestDTO.setChatLink(generateRandomChatLink());
//        carpoolRequestDTO.setCapacity(generateRandomCapacity());
//        carpoolRequestDTO.setCost(generateRandomCost());
//        return carpoolRequestDTO;
//    }
//
//    public static void main(String[] args) {
//        // 예시로 10개의 랜덤 카풀 요청 생성
//        for (int i = 0; i < 10; i++) {
//            CarpoolRequestDTO carpoolRequestDTO = generateRandomCarpoolRequestDTO();
//            System.out.println(carpoolRequestDTO);
//        }
//    }
//}
