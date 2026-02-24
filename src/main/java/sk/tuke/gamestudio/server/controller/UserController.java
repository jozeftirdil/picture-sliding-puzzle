package sk.tuke.gamestudio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.gamestudio.entity.User;

@Controller
@RequestMapping("/")
@Scope(WebApplicationContext.SCOPE_SESSION)
public class UserController {
    private User loggedUser;

    @RequestMapping
    public String index() {
        return "index";
    }

    @RequestMapping("/login")
    public String login(String login, String password) {
        if("heslo".equals(password)) {
            loggedUser = new User(login);
            return "redirect:/puzzle";
        }
        return "redirect:/";
    }

    @RequestMapping("/logout")
    public String logout() {
        loggedUser = null;

        return "redirect:/";
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public boolean isLogged() {
        return loggedUser != null;
    }
}
