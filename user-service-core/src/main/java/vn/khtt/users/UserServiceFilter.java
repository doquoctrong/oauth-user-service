package vn.khtt.users;

import com.googlecode.objectify.ObjectifyService;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserServiceFilter implements Filter {
  static{
    ObjectifyService.register(User.class);
    ObjectifyService.register(AuthProfile.class);
    ObjectifyService.register(FacebookAuthProfile.class);
  }
  
  private static ThreadLocal<HttpServletRequest> currentRequest = new ThreadLocal<HttpServletRequest>();
  private static ThreadLocal<HttpServletResponse> currentResponse = new ThreadLocal<HttpServletResponse>();

  public static HttpServletRequest getCurrentRequest() {
    return currentRequest.get();
  }

  public static HttpServletResponse getCurrentResponse() {
    return currentResponse.get();
  }
  
  private Map<String, AuthHanlder> authHanlders = new HashMap<String, AuthHanlder>();
  private Map<String, AuthHanlder> authCallbackHanlders = new HashMap<String, AuthHanlder>();
  
  public UserServiceFilter() {
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    AuthHanlder[] handlers = new AuthHanlder[]{
      new FacebookAuthHanlder()
    };
    
    for (AuthHanlder authHanlder : handlers){
      if (authHanlder.isConfigOK()){
        authHanlders.put(authHanlder.getAuthPath(), authHanlder);
        authCallbackHanlders.put(authHanlder.getAuthCallbackPath(), authHanlder);
      }
    }
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
    try {
      HttpServletRequest request = (HttpServletRequest)req;
      HttpServletResponse response = (HttpServletResponse)resp;
      currentRequest.set(request);
      currentResponse.set(response);
      
      String requestURI = request.getRequestURI();
      System.out.println(requestURI);
      
      AuthHanlder authHanlder = authHanlders.get(requestURI);
      if (authHanlder != null){
        authHanlder.processAuth(request, response);
        return;
      }
      authHanlder = authCallbackHanlders.get(requestURI);
      if (authHanlder != null){
        authHanlder.authCallback(request, response);
        return;
      }
      
      chain.doFilter(req, resp);
    }finally{
      currentRequest.remove();
      currentResponse.remove();
    }
  }

  @Override
  public void destroy() {
  }
}
