//package utilities;
//
//import play.mvc.Http;
//
//public class TestHelper {
//
//    private static TestHelper testHelper = null;
//
//    private TestHelper() {
//        //Private Constructor
//    }
//
//    public TestHelper getInstance() {
//        if (testHelper == null) {
//            testHelper = new TestHelper();
//        }
//        return testHelper;
//    }
//
//    public Http.RequestImpl buildCandidateRequest() {
//        return Helpers.fakeRequest()
//                .cookie(Http.Cookie.builder("username", "fakeName").build())
//                .cookie(Http.Cookie.builder("userType", "candidate").build())
//                .build();
//    }
//}
