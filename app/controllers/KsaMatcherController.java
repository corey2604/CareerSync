package controllers;

import models.JobDescription;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utilities.KsaMatcher;

import java.util.List;

public class KsaMatcherController extends Controller {

    public Result viewJobRecommendations(Http.Request request) {
        List<JobDescription> matchingJobDescriptions = KsaMatcher.getInstance().getJobRecommendations(request.cookie("username").value());
        return ok(views.html.candidate.jobReccomendations.render(matchingJobDescriptions));
    }
}
