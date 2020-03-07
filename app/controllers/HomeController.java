package controllers;

import awsWrappers.ClasspathPropertiesFileCredentialsProviderWrapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import utilities.FileHandler;
import utilities.LoginChecker;
import play.mvc.*;

import javax.inject.Inject;
import javax.swing.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    @Inject
    public HomeController() {
    }

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index(Http.Request request) {
        if (!LoginChecker.getInstance().isLoggedin(request)) {
            return redirect(routes.LogInController.logIn());
        }
        if (request.cookies().getCookie("userType").get().value().equals("candidate")) {
            return ok(views.html.candidate.index.render());
        } else {
            return ok(views.html.recruiter.recruiterIndex.render());
        }
    }

    public Result uploadFile(Http.Request request) {
         AmazonS3 s3Client = AmazonS3ClientBuilder
                .standard()
                .withRegion(Regions.EU_WEST_1)
                .withCredentials(ClasspathPropertiesFileCredentialsProviderWrapper.getInstance())
                .build();
        FileHandler.getInstance(s3Client, new JFileChooser()).uploadFile(request.cookie("username").value());
        return ok(views.html.candidate.index.render());
    }

    public Result logOut(Http.Request request) {
        return redirect(routes.LogInController.logIn()).discardingCookie("username").discardingCookie("userType");
    }
}
