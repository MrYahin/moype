package frontend;

import base.AuthService;
import base.DBService;
import base.dataSets.UserDataSet;
import dbService.DBServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthServiceImpl implements AuthService{

    private Map<String, String> userSessions 	= new HashMap<>();
	
	public boolean addUser(String userName, UserProfile userProfile) {
        
    	UserDataSet dataSet = new UserDataSet(1, userProfile.getLogin(), userProfile.getPassword(), userProfile.getEmail());
    	
    	DBService dbService = new DBServiceImpl();
    	
        String status = dbService.getLocalStatus();
        System.out.println(status);    	

        UserDataSet dataSetExist = dbService.readByName(userName);

        if (dataSetExist == null ){
   	        dbService.save(dataSet);
    		dbService.shutdown();
   	    	return true;
        }
   		else {
           	dbService.shutdown();
       		return false;
    	}        
    }
    
    public UserProfile getUser(String userName){
    	
    	UserDataSet dataSet;
    	
    	DBService dbService = new DBServiceImpl();
        String status = dbService.getLocalStatus();
        System.out.println(status); 
        dataSet = dbService.readByName(userName);
        
        if (dataSet != null) {
        	UserProfile userProfile = new UserProfile(dataSet.getName(),dataSet.getPassword(), dataSet.getEmail());
            return userProfile;
        }
        else {
        	return null;
        }
      
    }
    
    public String getUserFromSession(String sessionId) {
        return userSessions.get(sessionId);
    }    
}
