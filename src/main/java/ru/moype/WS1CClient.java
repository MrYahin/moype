package ru.moype;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import ru.moype.model.integration.GetSpec;
import ru.moype.model.integration.GetSpecResponse;

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
