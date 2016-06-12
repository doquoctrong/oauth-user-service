package vn.khtt.users.demo;

import com.github.scribejava.apis.FacebookApi;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import vn.khtt.users.User;
import vn.khtt.users.UserService;
import vn.khtt.users.UserServiceFactory;

public class DemoServlet extends HttpServlet {
  private static UserService userService = UserServiceFactory.getUserService();

  public DemoServlet() {
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    User user = userService.getCurrentUser();
    System.out.println("User: " + user);
    
    if (user == null){
//      response.getWriter().println("Please login: " + userService.createLoginURL("/", FacebookApi.instance()));
      response.sendRedirect(userService.createLoginURL("/", FacebookApi.instance()));
    }else{
      response.getWriter().println("User " + user);
    }
  }
}
