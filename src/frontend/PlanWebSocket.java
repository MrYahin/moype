package frontend;

import base.PlanMechanics;
import base.WebSocketService;
import base.AgentUser;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import org.json.simple.JSONObject;

@WebSocket
public class PlanWebSocket {

    private String Name;
    private Session session;
    private PlanMechanics planMechanics;
    private WebSocketService webSocketService;
	
    public PlanWebSocket(String Name, PlanMechanics planMechanics, WebSocketService webSocketService) {
        this.Name = Name;
        this.planMechanics = planMechanics;
        this.webSocketService = webSocketService;
    }
    
    public String getAgentName() {
        return Name;
    }    
	
    public void startPlan(AgentUser agent) {
        try {
            JSONObject jsonStart = new JSONObject();
            jsonStart.put("status", "start");
            jsonStart.put("AgentName", agent.getAgentName());
            session.getRemote().sendString(jsonStart.toJSONString());
        } catch (Exception e) {
            System.out.print(e.toString());
        }
    }
    
    public void PlanCompleted(AgentUser agent, boolean done) {
        try {
            JSONObject jsonStart = new JSONObject();
            jsonStart.put("status", "finished");
            jsonStart.put("done", done);
            session.getRemote().sendString(jsonStart.toJSONString());
        } catch (Exception e) {
            System.out.print(e.toString());
        }
    }
    
 //   @OnWebSocketMessage
 //   public void onMessage(String data) {
 //       PlanMechanics.incrementScore(myName);
 //   }
    
    @OnWebSocketConnect
    public void onOpen(Session session) {
        setSession(session);
        webSocketService.addAgent(this);
//        PlanMechanics.addAgent(Name);
    }
    
//    public void setMyScore(GameUser user) {
//        JSONObject jsonStart = new JSONObject();
//        jsonStart.put("status", "increment");
//        jsonStart.put("name", myName);
//        jsonStart.put("score", user.getMyScore());
//        try {
//            session.getRemote().sendString(jsonStart.toJSONString());
//        } catch (Exception e) {
//            System.out.print(e.toString());
//        }
//    }
    
//    public void setEnemyScore(GameUser user) {
//        JSONObject jsonStart = new JSONObject();
//        jsonStart.put("status", "increment");
//        jsonStart.put("name", user.getEnemyName());
//        jsonStart.put("score", user.getEnemyScore());
//        try {
//            session.getRemote().sendString(jsonStart.toJSONString());
//        } catch (Exception e) {
//            System.out.print(e.toString());
//        }
//    }
    
    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {

    }    
    
}
