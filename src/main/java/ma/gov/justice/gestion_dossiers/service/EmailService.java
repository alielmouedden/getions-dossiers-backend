package ma.gov.justice.gestion_dossiers.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendTemporaryPassword(String to, String tempPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("justice-hub@gov.ma");
        message.setTo(to);
        message.setSubject("Récupération de compte - Mot de passe temporaire");
        message.setText("Votre mot de passe temporaire est : " + tempPassword + "\n\nVeuillez le changer lors de votre prochaine connexion.");
        mailSender.send(message);
    }
}
