package controllers;

import utilities.FileUploader;
import utilities.LoginChecker;
import com.typesafe.config.Config;
import play.mvc.*;

import javax.inject.Inject;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    private final Config config;

    @Inject
    public HomeController(Config config) {
        this.config = config;
    }

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index(Http.Request request) {
        if (!LoginChecker.isLoggedin(request)) {
            return redirect(routes.LogInController.logIn());
        }
        if (request.cookies().getCookie("userType").get().value().equals("candidate")) {
            return ok(views.html.index.render());
        } else {
            return ok(views.html.recruiterIndex.render());
        }
    }

    public Result uploadFile(Http.Request request) {
        FileUploader.createFolder(request.cookie("username").value());
        FileUploader.uploadFile(request.cookie("username").value());
        return ok(views.html.index.render());
    }

    public Result logOut(Http.Request request) {
        return redirect(routes.LogInController.logIn()).discardingCookie("username").discardingCookie("userType");
    }
}
