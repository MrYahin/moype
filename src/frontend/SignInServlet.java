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

public class SignInServlet extends HttpServlet{
	
	private AuthService accountService;
	
	//Это конструктор класса
    public SignInServlet(AuthService authService) {
        this.accountService = authService;
    }	
	
	
	public void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    	
		String name 		= request.getParameter("name");
		String password 	= request.getParameter("password"); 

		Map<String, Object> pageVariables = new HashMap<>();        
		
		if (name == "") {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            pageVariables.put("loginStatus", "Wrong login/password");
		}
        else {
        	response.setStatus(HttpServletResponse.SC_OK);
    		UserProfile profile = accountService.getUser(name);
            if (profile != null && profile.getPassword().equals(password)) {
                pageVariables.put("loginStatus", "Login passed");
            } else {
                pageVariables.put("loginStatus", "Wrong login/password");
            }
        }

		response.getWriter().println(PageGenerator.getPage("auth/authstatus.html", pageVariables));
		
		response.setContentType("text/html;charset=utf-8");
		
		
    }

    
    public void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    	
    	
    }    
	

}
