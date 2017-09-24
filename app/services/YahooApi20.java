package services;

import com.github.scribejava.apis.google.GoogleJsonTokenExtractor;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.extractors.TokenExtractor;
import com.github.scribejava.core.model.OAuth2AccessToken;

/**
 * Created by cleclair on 2017-09-24.
 */
public class YahooApi20 extends DefaultApi20 {

    protected YahooApi20() {
    }

    private static class InstanceHolder {
        private static final YahooApi20 INSTANCE = new YahooApi20();
    }

    public static YahooApi20 instance() {
        return YahooApi20.InstanceHolder.INSTANCE;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return "https://api.login.yahoo.com/oauth2/get_token";
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return "https://api.login.yahoo.com/oauth2/request_auth";
    }

    @Override
    public TokenExtractor<OAuth2AccessToken> getAccessTokenExtractor() {
        return GoogleJsonTokenExtractor.instance();
    }
}