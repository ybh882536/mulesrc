package org.mule.module.oauth;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.findAll;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

import org.mule.module.http.HttpParser;
import org.mule.module.http.ParameterMap;
import org.mule.module.oauth.asserter.AuthorizationRequestAsserter;
import org.mule.module.oauth.asserter.OAuthStateFunctionAsserter;
import org.mule.security.oauth.OAuthConstants;
import org.mule.tck.junit4.rule.SystemProperty;

import com.github.tomakehurst.wiremock.client.WireMock;

import java.io.IOException;

import org.apache.http.client.fluent.Request;
import org.junit.Rule;
import org.junit.Test;

public class AuthorizationCodeMultitenantTestCase extends AbstractAuthorizationCodeFunctionalTestCase
{

    public static final String USER_ID_JOHN = "john";
    public static final String JOHN_ACCESS_TOKEN = "123456789";
    public static final String USER_ID_TONY = "tony";
    public static final String TONY_ACCESS_TOKEN = "abcdefghi";
    public static final String MULTITENANT_CONFIG = "multitenantConfig";

    @Rule
    public SystemProperty localAuthorizationUrl = new SystemProperty("local.authorization.url", String.format("http://localhost:%d/authorization", localHostPort.getNumber()));
    @Rule
    public SystemProperty authorizationUrl = new SystemProperty("authorization.url", String.format("http://localhost:%d" + AUTHORIZE_PATH, oauthServerPort.getNumber()));
    @Rule
    public SystemProperty redirectUrl = new SystemProperty("redirect.url", String.format("http://localhost:%d/redirect", localHostPort.getNumber()));
    @Rule
    public SystemProperty tokenUrl = new SystemProperty("token.url", String.format("http://localhost:%d" + TOKEN_PATH, oauthServerPort.getNumber()));


    @Override
    protected String getConfigFile()
    {
        return "authorization-code-multitenant-config.xml";
    }

    @Test
    public void danceWithCustomOAuthStateId() throws Exception
    {
        executeForUserWithAccessToken(USER_ID_JOHN, JOHN_ACCESS_TOKEN);
        WireMock.reset();
        executeForUserWithAccessToken(USER_ID_TONY, TONY_ACCESS_TOKEN);

        OAuthStateFunctionAsserter.createFrom(muleContext.getExpressionLanguage(), MULTITENANT_CONFIG, USER_ID_JOHN)
                .assertAccessTokenIs(JOHN_ACCESS_TOKEN)
                .assertState(null);
        OAuthStateFunctionAsserter.createFrom(muleContext.getExpressionLanguage(), MULTITENANT_CONFIG, USER_ID_TONY)
                .assertAccessTokenIs(TONY_ACCESS_TOKEN)
                .assertState(null);
    }

    @Test
    public void refreshToken() throws Exception
    {
        executeForUserWithAccessToken(USER_ID_JOHN, JOHN_ACCESS_TOKEN);
        WireMock.reset();
        executeForUserWithAccessToken(USER_ID_TONY, TONY_ACCESS_TOKEN);

        OAuthStateFunctionAsserter.createFrom(muleContext.getExpressionLanguage(), MULTITENANT_CONFIG, USER_ID_JOHN)
                .assertAccessTokenIs(JOHN_ACCESS_TOKEN)
                .assertState(null);
        OAuthStateFunctionAsserter.createFrom(muleContext.getExpressionLanguage(), MULTITENANT_CONFIG, USER_ID_TONY)
                .assertAccessTokenIs(TONY_ACCESS_TOKEN)
                .assertState(null);
    }

    private void executeForUserWithAccessToken(String userId, String accessToken) throws IOException
    {
        wireMockRule.stubFor(get(urlMatching(AUTHORIZE_PATH + ".*")).willReturn(aResponse().withStatus(200)));

        final String expectedState = ":oauthStateId=" + userId;
        Request.Get(localAuthorizationUrl.getValue() + "?userId=" + userId).execute();

        AuthorizationRequestAsserter.create((findAll(getRequestedFor(urlMatching(AUTHORIZE_PATH + ".*"))).get(0)))
                .assertStateIs(expectedState);

        wireMockRule.stubFor(post(urlEqualTo(TOKEN_PATH))
                                     .willReturn(aResponse()
                                                         .withBody("{" +
                                                                   "\"" + OAuthConstants.ACCESS_TOKEN_PARAMETER + "\":\"" + accessToken + "\"," +
                                                                   "\"" + OAuthConstants.EXPIRES_IN_PARAMETER + "\":" + EXPIRES_IN + "," +
                                                                   "\"" + OAuthConstants.REFRESH_TOKEN_PARAMETER + "\":\"" + REFRESH_TOKEN + "\"}")));

        final String redirectUrlQueryParams = HttpParser.encodeQueryString(new ParameterMap()
                                                                                   .putAndReturn(OAuthConstants.CODE_PARAMETER, AUTHENTICATION_CODE)
                                                                                   .putAndReturn(OAuthConstants.STATE_PARAMETER, expectedState));
        Request.Get(redirectUrl.getValue() + "?" + redirectUrlQueryParams).socketTimeout(1000000).execute();
    }

}
