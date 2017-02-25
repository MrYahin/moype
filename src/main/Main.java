package main;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import base.AuthService;
import base.PlanMechanics;
import base.WebSocketService;
import frontend.AuthServiceImpl; //Не совсем понятно, зачем это добавлять
import frontend.SignInServlet;
import frontend.SignUpServlet;
import frontend.WebSocketPlanServlet;
import frontend.WebSocketServiceImpl;
import mechanics.PlanMechanicsImpl;

public class Main {

	public static void main(String args[]) throws Exception {
		
		int port = 8080;
        if (args.length == 1) {
        	String portString = args[0];
        	port = Integer.valueOf(portString);
        } 
        
        System.out.append("Starting at port: ").append(String.valueOf(port)).append('\n');
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        //Authentication
        AuthService authService = new AuthServiceImpl();
		WebSocketService webSocketService = new WebSocketServiceImpl();
		PlanMechanics PlanMechanics = new PlanMechanicsImpl(webSocketService);
        
        context.addServlet(new ServletHolder(new SignInServlet(authService)), "/auth/signIn");
        context.addServlet(new ServletHolder(new SignUpServlet(authService)), "/auth/signUp");
        context.addServlet(new ServletHolder(new WebSocketPlanServlet(authService, PlanMechanics, webSocketService)), "/start");
        
        //Можно создать папку "public_html" и основную страницу
        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setResourceBase("public_html");        
        
        //Не совсем понимаю зачем это нужно???
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler, context});             
        
		Server server = new Server(port);        
        server.setHandler(handlers);
        server.start();
        server.join();   
        
        //run Planning in main thread
        PlanMechanics.run();
	}
	
}
