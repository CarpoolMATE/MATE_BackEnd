package MATE.Carpool.common.email;

import jakarta.mail.MessagingException;
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

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendEmailForgotPassword(String to, String password) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String subject = "🚗 MATE 임시 비밀번호를 보내드립니다. ";


        String content ="""
         <div style="max-width: 500px; margin: auto; padding: 20px; 
        border: 1px solid #ddd; border-radius: 10px;
        font-family: Arial, sans-serif; background-color: #f9f9f9; text-align: center;">
                <h2 style="color: #333;">🚗 MATE 에서  비밀번호 발급해 드립니다.</h2>
                <div style="margin: 20px 0; padding: 15px; font-size: 24px; 
        font-weight: bold; color: #ffffff; background-color: #007bff;
        display: inline-block; border-radius: 5px;">
                %s
                </div>
                <hr style="margin: 20px 0;">
                <p style="font-size: 12px; color: #999;"> 메이트 팀 </p>
                </div>
                """.formatted(password);

                helper.setFrom("mical0108@gmail.com");
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(content, true);

                mailSender.send(message);
            }
        }

