package frontend;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import base.AuthService;
import utils.PageGenerator;

public class SignUpServlet extends HttpServlet{
	
	private AuthService accountService;
	
	//Это конструктор класса
    public SignUpServlet(AuthService authService) {
        this.accountService = authService;
    }	
	
	
	public void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    	
		String name 		= request.getParameter("name");
		String password 	= request.getParameter("password"); 
		String email 		= request.getParameter("email"); 

		Map<String, Object> pageVariables = new HashMap<>();        
		
		UserProfile userProfile = new UserProfile(name, password, email);
	
    	if (name == null) {
    		pageVariables.put("signUpStatus", "Check sign up form");
    	}
    	else {	
			if (accountService.addUser(name, userProfile)) {
				pageVariables.put("signUpStatus", "New user created");
				response.setStatus(HttpServletResponse.SC_OK);
			}
	        else {
				pageVariables.put("signUpStatus", "User with name: " + name + " already exists");
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			}
    	}
		response.getWriter().println(PageGenerator.getPage("auth/signupstatus.html", pageVariables));
		response.setContentType("text/html;charset=utf-8");
    }
    
    public void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    	
    	
    }    
}
