package ru.moype;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import ru.moype.model.GetSpec;
import ru.moype.model.GetSpecResponse;

@Component
public class WS1CClient extends WebServiceGatewaySupport {

    @Value("${webservices.uri}")
    private String wsUri;

    public GetSpecResponse getResponse()
    {
        GetSpec request = new GetSpec();

        GetSpecResponse response = (GetSpecResponse) getWebServiceTemplate().marshalSendAndReceive(wsUri, request,null);

        return response;
    }
}
