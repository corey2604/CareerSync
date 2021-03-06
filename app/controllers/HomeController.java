package controllers;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import enums.CareerSyncErrorMessages;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import scala.Option;
import utilities.DynamoAccessor;
import utilities.FileHandler;
import utilities.KsaMatcher;
import utilities.LoginChecker;

import javax.inject.Inject;
import javax.swing.*;
import java.util.Optional;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    private static final AmazonS3 S3_CLIENT = AmazonS3ClientBuilder
            .standard()
            .withRegion(Regions.EU_WEST_1)
            .build();

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
            boolean uploadedCV = FileHandler.getInstance(S3_CLIENT, new JFileChooser())
                    .doesUserHaveUploadedCV(request.cookies().getCookie("username").get().value());
            boolean completedKsas = DynamoAccessor.getInstance().doesUserHaveKsas(request.cookies().getCookie("username").get().value());
            return ok(views.html.candidate.index.render(uploadedCV, completedKsas, Optional.empty()));
        } else {
            return ok(views.html.recruiter.recruiterIndex.render());
        }
    }

    public Result uploadFile(Http.Request request) {
        boolean successfullyUploaded = FileHandler.getInstance(S3_CLIENT, new JFileChooser()).uploadFile(request.cookie("username").value());
        if (successfullyUploaded) {
            return ok(views.html.candidate.index.render(successfullyUploaded, successfullyUploaded, Optional.empty()));
        }
        return ok(views.html.candidate.index.render(successfullyUploaded, successfullyUploaded, Optional.of(CareerSyncErrorMessages.UNABLE_TO_UPLOAD_CV)));
    }

    public Result viewCv(Http.Request request) {
        FileHandler.getInstance(S3_CLIENT, new JFileChooser()).getFileFromUsername(request.cookie("username").value());
        return ok(views.html.candidate.index.render(true, true, Optional.empty()));
    }

    public Result viewCvForUser(Http.Request request, String username, String jobTitle) {
        FileHandler.getInstance(S3_CLIENT, new JFileChooser()).getFileFromUsername(username);
        return redirect(routes.JobDescriptionController.getPotentialCandidates(request.cookie("username").value(), jobTitle));
    }

    public Result logOut(Http.Request request) {
        return redirect(routes.LogInController.logIn()).discardingCookie("username").discardingCookie("userType");
    }
}
