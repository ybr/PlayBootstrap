import daos._

package object App {
  object Daos {
    // Postgre
    implicit val userDAO = UserPostgreDAO
    implicit val adminDAO: AdminDAO = AdminPostgreDAO

    // Mongo
    //   implicit val userDAO: UserDAO = UserMongoDAO
    //   implicit val adminDAO: AdminDAO = AdminMongoDAO
  }
}
