import controllers._
import services._
import daos._


package object Controllers {
  val Users = new controllers.Users {
    override def userService = Services.userService
  }

  val Visitors = new controllers.Visitors {
    override def userService = Services.userService
  }

  val Authentication = new controllers.Authentication {
    override def userService = Services.userService
  }
}

package object Services {
  val userService = new UserService { override def userDAO = Daos.userDAO }
}

// Postgre
package object Daos {
  // val userDAO = UserPostgreDAO
  val userDAO = UserMongoDAO
  val adminDAO: AdminDAO = AdminPostgreDAO
}

// Mongo
// package object Daos {
//   val userDAO = UserMongoDAO
//   val adminDAO = AdminMongoDAO
// }
