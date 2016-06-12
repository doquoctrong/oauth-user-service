package vn.khtt.users;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import com.googlecode.objectify.Key;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.Iterator;

import javax.servlet.http.Cookie;

public class FacebookAuthHanlder extends AuthHanlder.BaseAuthHanlder implements AuthHanlder {
  private static final String FACEBOK_SCOPE = "email, public_profile, user_friends";
  private static final String FACEBOK_PROFILE_URL = "https://graph.facebook.com/v2.5/me";
  
  private ObjectMapper objectMapper = new ObjectMapper();
  
  public FacebookAuthHanlder(){
    UserServiceConfig config = UserServiceConfig.getInstance();
    appId = config.getProps().getProperty("facebook.app-id");
    appSecret = config.getProps().getProperty("facebook.app-secret");
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
  }
  
  @Override
  public void processAuth(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    OAuth20Service service = this.getOAuth20Service(request);
    String authorizationUrl = service.getAuthorizationUrl();

    response.sendRedirect(authorizationUrl);
  }

  @Override
  public void authCallback(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    String error = request.getParameter("error");
    if (error != null){
      response.sendRedirect("/demo");   // TODO
      return;
    }
    
    OAuth20Service service = this.getOAuth20Service(request);
    String code = request.getParameter("code");
    OAuth2AccessToken accessToken = service.getAccessToken(code);
    OAuthRequest oauthRequest = new OAuthRequest(Verb.GET, FACEBOK_PROFILE_URL, service);
    oauthRequest.addParameter("fields", "id,email,first_name,last_name,name,gender,timezone,verified,updated_time,locale,link");
    service.signRequest(accessToken, oauthRequest);
    Response oauthResponse = oauthRequest.send();
    
    // Save
    String json = oauthResponse.getBody();
    System.out.println(json);
    AuthProfile authProfile = objectMapper.readValue(json, FacebookAuthProfile.class);
//    authProfile.setAccessToken(new AccessToken(accessToken));
//    ofy().save().entity(authProfile).now();
//    Key<? extends AuthProfile> authProfileKey = Key.create(AuthProfile.class, authProfile.getId());
//    User user = ofy().load().type(User.class).filter("authProfileKeys", authProfileKey).first().now();
//    if (user == null){
//      user = new User();
//      user.setName(authProfile.getName());
//      user.setEmail(authProfile.getEmail());
//      user.getAuthProfileKeys().add(authProfileKey);
//    }else{
//      // Update AuthProfile
//      for (Iterator<Key<? extends AuthProfile>> iterator = user.getAuthProfileKeys().iterator(); iterator.hasNext(); ) {
//        Key<? extends AuthProfile> k = iterator.next();
//        if (authProfileKey.equals(k)){
//          iterator.remove();
//        }
//      }
//      user.getAuthProfileKeys().add(authProfileKey);
//    }
//    ofy().save().entity(user).now();
    User user = saveAuthProfile(authProfile, new AccessToken(accessToken));
    
    Cookie cookie = new Cookie(Constants.COOKIE_NAME, "" + user.getId());
    cookie.setPath("/");
    cookie.setMaxAge(3600);
    response.addCookie(cookie);
    
    String returnUrl = this.getReturnUrl(request);
    response.sendRedirect(returnUrl);
  }
  
  private OAuth20Service getOAuth20Service(HttpServletRequest request){
    String baseUrl = Utils.getRequestBaseUrl(request);
    String callbackUrl = baseUrl + this.getAuthCallbackPath();
    
    String state = this.generateState(request);
    
    OAuth20Service service = new ServiceBuilder().apiKey(appId).apiSecret(appSecret)
      .state(state).scope(FACEBOK_SCOPE)
      .callback(callbackUrl).build(FacebookApi.instance());
    
    return service;
  }
}
