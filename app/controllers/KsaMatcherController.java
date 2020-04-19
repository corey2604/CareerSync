package controllers;

import models.JobDescription;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utilities.KsaMatcher;

import java.util.List;
import java.util.Optional;

public class KsaMatcherController extends Controller {

    public Result viewJobRecommendations(Http.Request request) {
        String username = request.cookie("username").value();
        List<JobDescription> matchingJobDescriptions = KsaMatcher.getInstance().getJobRecommendations(username);
        return ok(views.html.candidate.jobReccomendations.render(username, matchingJobDescriptions, Optional.empty()));
    }
}
