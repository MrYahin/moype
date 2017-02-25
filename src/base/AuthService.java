package base;

import frontend.UserProfile;

public interface AuthService {

	boolean addUser(String userName, UserProfile userProfile);
	UserProfile getUser(String userName);
//	UserProfile getSessions(String sessionId);	
	String getUserFromSession(String sessionId);
}
