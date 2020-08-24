import org.eclipse.jetty.server.Server; 
import org.eclipse.jetty.servlet.ServletContextHandler; 
import org.eclipse.jetty.servlet.ServletHolder; 
 
import servlet.FirstServlet; 
 

public class ServletServer { 
    public static void main(String[] args) throws Exception { 
        Server server = new Server(8086);   
 
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS); 
        context.setContextPath("/");   
 
        server.setHandler(context);   
 
        context.addServlet(new ServletHolder(new FirstServlet()), "/work"); 
        //context.addServlet(new ServletHolder(new FirstServlet()), "/verify"); 
        //context.addServlet(new ServletHolder(new FirstServlet()), "/otherpath");   
 
        server.start(); 
        server.join(); 
    } 
} 