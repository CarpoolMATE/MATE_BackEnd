package MATE.Carpool.common.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendEmailNotice(String email, String newPassword) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("임시 비밀번호 발급 안내"); // 이메일 제목
            mimeMessageHelper.setText(setContext(todayDate(), newPassword), true); // 이메일 본문, 비밀번호 포함
            javaMailSender.send(mimeMessage);

            log.info("Succeeded to send Email to {}", email);
        } catch (Exception e) {
            log.error("Failed to send Email to {}", email, e);
            throw new RuntimeException("이메일 전송 실패");
        }
    }

        public String todayDate(){
            ZonedDateTime todayDate = LocalDateTime.now(ZoneId.of("Asia/Seoul")).atZone(ZoneId.of("Asia/Seoul"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 d일");
            return todayDate.format(formatter);
        }

        //thymeleaf를 통한 html 적용
        public String setContext(String date,String password) {
            Context context = new Context();
            context.setVariable("date", date);
            context.setVariable("tentative", password);
            return templateEngine.process("email", context);
        }
    }


