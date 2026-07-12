package com.origem.backend.service;

import com.origem.backend.domain.Signup;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPaymentConfirmation(Signup signup) {
        boolean pt = "pt".equals(signup.getLang());
        String safeName = HtmlUtils.htmlEscape(signup.getName());
        String subject = pt ? "Pagamento confirmado - Roots" : "Payment confirmed - Roots";
        String body = pt
                ? "Olá " + safeName + ",<br><br>Seu pagamento da taxa de fundador foi confirmado e seu acesso antecipado à Roots está garantido.<br><br>Equipe Roots"
                : "Hi " + safeName + ",<br><br>Your founding fee payment has been confirmed and your early access to Roots is secured.<br><br>The Roots team";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setTo(signup.getEmail());
            helper.setFrom("no-reply@origem.app");
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
        } catch (Exception e) {
            log.warn("Falha ao enviar e-mail de confirmação para {}: {}", signup.getEmail(), e.getMessage());
        }
    }
}
