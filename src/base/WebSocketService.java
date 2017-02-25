package base;

import frontend.PlanWebSocket;

public interface WebSocketService {

	//?
	//void signIn();
	//?
	//void signUp();
	
	void addAgent(PlanWebSocket user);
	
    void notifyPlanStart(AgentUser agent);
	
    void notifyPlanOver(AgentUser agent, boolean done);

    void notifyPlanEvent(AgentUser agent);
	
}
