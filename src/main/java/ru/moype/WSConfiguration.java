package ru.moype;


import org.apache.http.auth.UsernamePasswordCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

@Configuration
public class WSConfiguration {

    @Value("${webservices.uri}")
    private String defaultUri;
    
    @Autowired
    private WebServiceMessageSenderWithAuth webServiceMessageSenderWithAuth;

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // this package must match the package in the <generatePackage> specified in
        // pom.xml
        marshaller.setContextPath("ru.moype.model.integration");
        return marshaller;
    }

    @Bean
    @Primary
    public WS1CClient ws1CClient(Jaxb2Marshaller marshaller) {
        WS1CClient client = new WS1CClient();
        client.setDefaultUri(defaultUri);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        // set a HttpComponentsMessageSender which provides support for basic authentication
        client.setMessageSender(webServiceMessageSenderWithAuth);


        return client;
    }

//    @Bean
//    public HttpComponentsMessageSender httpComponentsMessageSender() {
//        HttpComponentsMessageSender httpComponentsMessageSender = new HttpComponentsMessageSender();
//        // set the basic authorization credentials
//        httpComponentsMessageSender.setCredentials(usernamePasswordCredentials());
//
//        return httpComponentsMessageSender;
//    }
//
//
//
//    @Bean
//    public UsernamePasswordCredentials usernamePasswordCredentials() {
//        // pass the user name and password to be used
//        return new UsernamePasswordCredentials(userName, userPassword);
//    }
}
