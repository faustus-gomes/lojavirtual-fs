package jdev.lojavirtual_fs.lojavirtual_fs.service;

import jakarta.mail.*;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.InterfaceAddress;
import java.util.Properties;

@Service
public class ServiceSendEmail {
    private String userName = "netgomaspb@gmail.com";
    private String senha = "vhrd vexf lnuj tuoi";

    /* Para gmails, habilitar acesso a app menos seguro
    Deixar ATIVADA
    https://myaccount.google.com/lesssecureapps?pli=1&rapt=AEjHL4NcGDC8Jl532fRNPDlX-0oLsqHBFIwRVN5Aup9Un41qj6g_jWwFYJDmbev6OJ5EytVGql0pF4usEFiR3oqPeG-DphEdsswPJ6iBlzTeP2yX1Im0Low
    */
    @Async
    public void enviarEmailHtml(String assunto, String menssagen, String emailDestino) {
      try {
          Properties properties = new Properties();
          properties.put("mail.smtp.ssl.trust", "*");
          properties.put("mail.smtp.auth", "true");
          properties.put("mail.smtp.starttls", "false");
          properties.put("mail.smtp.host", "smtp.gmail.com");
          properties.put("mail.smtp.port", "465");
          properties.put("mail.smtp.socketFactory.port", "465");
          properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

          Session session = Session.getInstance(properties, new Authenticator() {
              @Override
              protected PasswordAuthentication getPasswordAuthentication() {
                  return new PasswordAuthentication(userName, senha);
              }
          });

          session.setDebug(true);
          Address[] toUser = InternetAddress.parse(emailDestino);
          Message message = new MimeMessage(session);
          message.setFrom(new InternetAddress(userName, "Fausto - FaustusGomesDev", "UTF-8"));
          message.setRecipients(Message.RecipientType.TO, toUser);
          message.setSubject(assunto);
          message.setContent(menssagen, "text/html; charset=utf-8");

          Transport.send(message);
      } catch (Exception e) {
          // Apenas loga o erro, não quebra o fluxo principal
          System.err.println("Erro ao enviar e-mail para " + emailDestino + ": " + e.getMessage());
      }
    }
}
