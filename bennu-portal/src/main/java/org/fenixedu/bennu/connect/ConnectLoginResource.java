package org.fenixedu.bennu.connect;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.IllformedLocaleException;
import java.util.Locale;
import java.util.Locale.Builder;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.UserProfile;
import org.fenixedu.bennu.core.rest.JsonBodyReaderWriter;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.portal.servlet.PortalLoginServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;

import static org.fenixedu.bennu.core.json.JsonUtils.getString;
import static org.fenixedu.bennu.core.util.CoreConfiguration.supportedLocales;
import static org.fenixedu.bennu.portal.BennuPortalConfiguration.getConfiguration;

@Path("/connect-login")
public class ConnectLoginResource {

    private static final Logger logger = LoggerFactory.getLogger(ConnectLoginResource.class);

    @GET
    @Path("/{callback}")
    public Response connectLogin(@PathParam("callback") String callback, @QueryParam("code") String code,
            @Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException, URISyntaxException {

        // We should always have a code here, so fail fast if not
        if (Strings.isNullOrEmpty(code)) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        // Check the callback is valid
        Optional<String> cb = decode(callback).filter(PortalLoginServlet::validateCallback);
        if (!cb.isPresent()) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        String actualCallback = cb.get();

        String authHeader = "Basic " + Base64.getEncoder().encodeToString((getConfiguration().connectClientId() + ":" +
                getConfiguration().connectClientSecret()).getBytes(StandardCharsets.UTF_8));
        String redirectUri = request.getRequestURL().toString();
        Client client = ClientBuilder.newBuilder().register(JsonBodyReaderWriter.class).build();
        try {
            JsonObject tokens = client.target(getConfiguration().connectBaseUrl()).path("/oauth/token").request()
                    .header("Authorization", authHeader).post(Entity
                            .form(new Form().param("grant_type", "authorization_code").param("code", code)
                                    .param("redirect_uri", redirectUri)), JsonObject.class);
            String accessToken = tokens.get("access_token").getAsString();
            JsonObject userInfo = client.target(getConfiguration().connectBaseUrl()).path("/connect/user").request()
                    .header("Authorization", "Bearer " + accessToken).get(JsonObject.class);
            User user = retrieveUserFromInfo(userInfo);
            Authenticate.login(request, response, user);
            logger.trace("Logged in user {}, redirecting to {}", user.getUsername(), actualCallback);
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            actualCallback = actualCallback + (actualCallback.contains("?") ? "&" : "?") + "login_failed=true";
        } finally {
            client.close();
        }
        return Response.status(Status.FOUND).location(new URI(actualCallback)).build();
    }

    private static Optional<String> decode(String base64Callback) {
        try {
            return Optional.of(new String(Base64.getUrlDecoder().decode(base64Callback), StandardCharsets.UTF_8));
        } catch (IllegalArgumentException e) {
            // Invalid Base64, return an empty Optional
            return Optional.empty();
        }
    }

    private User retrieveUserFromInfo(JsonObject userInfo) {
        String username = userInfo.get("username").getAsString();
        User user = User.findByUsername(username);
        if (user == null) {
            return initializeUser(username, userInfo);
        } else {
            getOrUpdateProfile(user.getProfile(), userInfo);
            return user;
        }
    }

    @Atomic(mode = TxMode.WRITE)
    private User initializeUser(String username, JsonObject userInfo) {
        User user = User.findByUsername(username);
        if (user != null) {
            getOrUpdateProfile(user.getProfile(), userInfo);
            return user;
        } else {
            return new User(username, getOrUpdateProfile(null, userInfo));
        }
    }

    @Atomic
    private UserProfile getOrUpdateProfile(UserProfile profile, JsonObject userInfo) {
        String givenNames = getString(userInfo, "givenNames");
        String familyNames = getString(userInfo, "familyNames");
        String displayName = getString(userInfo, "displayName");
        String email = getString(userInfo, "email");
        Locale locale = toLocale(getString(userInfo, "preferredLocale"));
        if (profile != null) {
            profile.changeName(givenNames, familyNames, displayName);
            profile.setEmail(email);
            if (locale != null) {
                profile.setPreferredLocale(locale);
            }
            return profile;
        } else {
            return new UserProfile(givenNames, familyNames, displayName, email, locale);
        }
    }

    private Locale toLocale(String localeTag) {
        try {
            if (localeTag != null) {
                Locale locale = new Builder().setLanguageTag(localeTag).build();
                if (supportedLocales().contains(locale)) {
                    return locale;
                }
            }
        } catch (IllformedLocaleException ignored) {
        }
        return null;
    }
}
