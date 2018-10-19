package org.fenixedu.bennu.core.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.core.domain.User;

import com.google.gson.Gson;

/**
 * Created by Sérgio Silva (hello@fenixedu.org).
 */
public class LoginBlockerContext {

    private final List<LoginBlocker> loginBlockers;

    public LoginBlockerContext() {
        this.loginBlockers = new ArrayList<>();
    }

    public void register(LoginBlocker loginBlocker) {
        this.loginBlockers.add(loginBlocker);
    }

    public boolean unregister(LoginBlocker loginBlocker) {
        return this.loginBlockers.remove(loginBlocker);
    }

    public List<String> checkBlockers(User user) {
        return loginBlockers.stream().flatMap(b -> b.getLoginBlockers(user).stream()).collect(Collectors.toList());
    }

    public void tryToBlock(HttpServletRequest request, HttpServletResponse response, User user) {
        final List<String> blockers = checkBlockers(user);
        if (!blockers.isEmpty()) {
            try {
                Authenticate.logout(request, response);
                response.sendError(418, new Gson().toJson(blockers)); //i'm a teapot
            } catch (IOException e) {
                throw new Error(e);
            }
        }
    }
}
