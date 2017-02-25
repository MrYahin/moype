package mechanics;

import base.PlanMechanics;
import base.WebSocketService;
import base.AgentUser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
//import utils.TimeHelper;

public class PlanMechanicsImpl implements PlanMechanics {

//    private static final int STEP_TIME = 100;

//    private static final int gameTime = 15 * 1000;

    private WebSocketService webSocketService;

//    private Map<String, PlanSession> nameToGame = new HashMap<>();

//    private Set<PlanSession> allSessions = new HashSet<>();

 //  private String waiter;

    public PlanMechanicsImpl(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    public void addAgent(String user) {
//        if (waiter != null) {
//            starGame(user);
//            waiter = null;
//        } else {
//            waiter = user;
 //       }
    }

//    public void incrementScore(String userName) {
//        PlanSession myGameSession = nameToGame.get(userName);
//        AgentUser myUser = myGameSession.getSelf(userName);
//        myUser.incrementMyScore();
//        AgentUser enemyUser = myGameSession.getEnemy(userName);
//        enemyUser.incrementEnemyScore();
//        webSocketService.notifyMyNewScore(myUser);
//        webSocketService.notifyEnemyNewScore(enemyUser);
//    }

    @Override
    public void run() {
        while (true) {
//            gmStep();
//            TimeHelper.sleep(STEP_TIME);
        }
    }

//    private void gmStep() {
//        for (PlanSession session : allSessions) {
//            if (session.getSessionTime() > gameTime) {
//                boolean firstWin = session.isFirstWin();
//                webSocketService.notifyGameOver(session.getFirst(), firstWin);
//                webSocketService.notifyGameOver(session.getSecond(), !firstWin);
//            }
//        }
//    }

 //   private void starGame(String first) {
 //       String second = waiter;
 //       PlanSession gameSession = new PlanSession(first, second);
 //       allSessions.add(gameSession);
 //       nameToGame.put(first, gameSession);
 //       nameToGame.put(second, gameSession);

//        webSocketService.notifyStartGame(gameSession.getSelf(first));
//        webSocketService.notifyStartGame(gameSession.getSelf(second));
//    }	
	
}
