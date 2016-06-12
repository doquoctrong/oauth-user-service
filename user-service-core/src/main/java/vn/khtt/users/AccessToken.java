package vn.khtt.users;

import com.github.scribejava.core.model.OAuth2AccessToken;

import com.googlecode.objectify.annotation.Index;

public class AccessToken {
  private String accessToken;
  
  private String tokenType;
  
  private String refreshToken;
  
  private String scope;
  
  // Expire 
  private Integer expiresIn;
  
  @Index
  private long expired;

  public AccessToken() {
  }

  public AccessToken(OAuth2AccessToken accessToken) {
    this.accessToken = accessToken.getAccessToken();
    this.tokenType = accessToken.getTokenType();
    this.refreshToken = accessToken.getRefreshToken();
    this.scope = accessToken.getScope();

    this.expiresIn = accessToken.getExpiresIn();
    this.expired = System.currentTimeMillis() + (expiresIn != null ? expiresIn * 1000 : 0);
  }

  public String getAccessToken() {
    return accessToken;
  }

  public String getTokenType() {
    return tokenType;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public String getScope() {
    return scope;
  }

  public Integer getExpiresIn() {
    return expiresIn;
  }

  public long getExpired() {
    return expired;
  }
}
