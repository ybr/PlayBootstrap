import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

class VisitorsSpec extends Specification {

  "Viitors signup" should {
    "respond with bad request if the login is missing" in new WithApplication {
      val response = route(FakeRequest(POST, "/signup").withFormUrlEncodedBody("email" -> "user@domain.com")).get
      status(response) must be equalTo(400)
    }

    "respond with bad request if the email is missing" in new WithApplication {
      val response = route(FakeRequest(POST, "/signup").withFormUrlEncodedBody("password" -> "pwd")).get
      status(response) must be equalTo(400)
    }

    "redirect to signin page if the signup succeeded" in new WithApplication {
      val response = route(FakeRequest(POST, "/signup").withFormUrlEncodedBody(
        "firstname" -> "y",
        "lastname" -> "br",
        "email" -> "user@domain.com",
        "password" -> "pwd"
      )).get
      status(response) must be equalTo(303)
      flash(response).get("success") must beSome
    }

    "respond with a bad request if the login already exists" in new WithApplication {
      route(FakeRequest(POST, "/signup").withFormUrlEncodedBody("email" -> "user@domain.com", "password" -> "pwd")).get
      val response = route(FakeRequest(POST, "/signup").withFormUrlEncodedBody("email" -> "user@domain.com", "password" -> "otherPwd")).get
      status(response) must be equalTo(400)
    }
  }
}
