package frontend;

import javax.servlet.annotation.WebServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import base.AuthService;
import base.PlanMechanics;
import base.WebSocketService;

/**
 * This class represents a servlet starting a webSocket application
 */

@WebServlet(name = "WebSocketPlanServlet", urlPatterns = {"/start"})
public class WebSocketPlanServlet extends WebSocketServlet{
	
	private final static int IDLE_TIME = 60 * 1000;
	private AuthService authService;
    private PlanMechanics planMechanics;
    private WebSocketService webSocketService;

    public WebSocketPlanServlet(AuthService authService,
                                PlanMechanics planMechanics,
                                WebSocketService webSocketService) {
        this.authService = authService;
        this.planMechanics = planMechanics;
        this.webSocketService = webSocketService;
    }

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(IDLE_TIME);
        factory.setCreator(new PlanWebSocketCreator(authService, planMechanics, webSocketService));
    }	    
    
}
