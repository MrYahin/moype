package frontend;

import base.WebSocketService;
import base.AgentUser;

import java.util.HashMap;
import java.util.Map;

public class WebSocketServiceImpl implements WebSocketService {

    private Map<String, PlanWebSocket> userSockets = new HashMap<>();

    public void addAgent(PlanWebSocket agent) {
        userSockets.put(agent.getAgentName(), agent);
    }

    public void notifyPlanEvent(AgentUser agent) {
       // userSockets.get(agent.getAgentName()).setMyScore(user);
    }

//    public void notifyEnemyNewScore(GameUser user) {
//        userSockets.get(user.getMyName()).setEnemyScore(user);
//    }

    public void notifyPlanStart(AgentUser agent) {
        PlanWebSocket planWebSocket = userSockets.get(agent.getAgentName());
        planWebSocket.startPlan(agent);
    }

    @Override
    public void notifyPlanOver(AgentUser agent, boolean done) {
        userSockets.get(agent.getAgentName()).PlanCompleted(agent, done);
    }	
	
}
