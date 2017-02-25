package frontend;

import base.AuthService;
import base.PlanMechanics;
import base.WebSocketService;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

public class PlanWebSocketCreator implements WebSocketCreator{

    private AuthService authService;
    private PlanMechanics planMechanics;
    private WebSocketService webSocketService;

    public PlanWebSocketCreator(AuthService authService,
                                PlanMechanics planMechanics,
                                WebSocketService webSocketService) {
        this.authService = authService;
        this.planMechanics = planMechanics;
        this.webSocketService = webSocketService;
    }

    @Override
    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
        String sessionId = req.getHttpServletRequest().getSession().getId();
        String name = authService.getUserFromSession(sessionId);					//ѕолучение имени пользовател€ по HTTP сессии
        return new PlanWebSocket(name, planMechanics, webSocketService);
    }	
}
