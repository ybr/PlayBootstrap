package daos

import play.api.test._

trait DaoSpec extends FutureAwaits with DefaultAwaitTimeout {
  abstract class WithInMemoryDB extends WithApplication(
    app = FakeApplication(
      additionalConfiguration = Helpers.inMemoryDatabase(options = Map("MODE" -> "PostgreSQL"))
    )) {
    // body
  }
}
