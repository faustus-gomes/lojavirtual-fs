package jdev.lojavirtual_fs.lojavirtual_fs.service;

import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import javax.net.ssl.*;
import java.io.Serializable;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HostIgnoringClient implements Serializable {
    private static final long serialVersionUID = 1L;
    private String hostName;
    public HostIgnoringClient(String hostName) {
        this.hostName = hostName;
    }

    public Client hostIgnoringClient() throws Exception {

        // 1. Configura o TrustManager que ignora certificados
        TrustManager[] trustManagers = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                        // Aceita todos os certificados de cliente
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                        // Aceita todos os certificados de servidor
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0]; // Array vazio = aceita todos
                    }
                }
        };

        // 2. Cria o SSLContext com TLS
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagers, new SecureRandom());

        // 3. Configura o HostnameVerifier global (igual ao código antigo)
        Set<String> hostNameList = new HashSet<String>();
        hostNameList.add(this.hostName);

        HttpsURLConnection.setDefaultHostnameVerifier(new IgnoreHostNameSSL(hostNameList));
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

        // 4. Cria o ClientConfig (substituto do DefaultClientConfig)
        ClientConfig config = new ClientConfig();

        // 5. Adiciona as classes que estavam em config.getClasses()
        config.register(JacksonJsonProvider.class);     // Para suporte JSON
        config.register(MultiPartFeature.class);         // Para suporte MultiPart

        // 6. Configura as propriedades (substitui o HTTPSProperties)
        // No Jersey moderno, isso é feito via propriedades ou diretamente no builder
        config.property(ClientProperties.CONNECT_TIMEOUT, 30000);
        config.property(ClientProperties.READ_TIMEOUT, 30000);

        // 7. Cria o HostnameVerifier para o cliente (substitui o HTTPSProperties)
        HostnameVerifier clientHostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true; // Aceita qualquer hostname
            }
        };
        Client client = ClientBuilder.newBuilder()
                .withConfig(config)
                .sslContext(sslContext)
                .hostnameVerifier(clientHostnameVerifier)
                .build();

        return client; // RETORNA o client, não null!
    }

}
