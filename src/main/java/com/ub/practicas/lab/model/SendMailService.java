package com.ub.practicas.lab.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SendMailService {
    
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmergencyEmail(String to, String body) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("DisExe");
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject("¡PACIENTE EN EMERGENCIA!");
        simpleMailMessage.setText(body);

        javaMailSender.send(simpleMailMessage);
    }
    
}
