package org.fenixedu.bennu.connect;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriBuilder;

import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.BennuPortalConfiguration;
import org.fenixedu.bennu.portal.login.LoginProvider;

import com.google.common.base.Strings;

public class ConnectLoginProvider implements LoginProvider {

    @Override
    public void showLogin(HttpServletRequest request, HttpServletResponse response, String callback)
            throws IOException, ServletException {
        if (Strings.isNullOrEmpty(callback)) {
            callback = CoreConfiguration.getConfiguration().applicationUrl();
        }
        UriBuilder builder = UriBuilder.fromUri(BennuPortalConfiguration.getConfiguration().connectBaseUrl() + "/oauth/authorize")
                .queryParam("client_id", BennuPortalConfiguration.getConfiguration().connectClientId())
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", CoreConfiguration.getConfiguration().applicationUrl() +
                        "/api/connect-login/" + Base64.getUrlEncoder().encodeToString(callback.getBytes(StandardCharsets.UTF_8)));
        response.sendRedirect(builder.build().toString());
    }

    @Override
    public String getKey() {
        return "connect";
    }

    @Override
    public String getName() {
        return "FenixEdu Connect";
    }
}
