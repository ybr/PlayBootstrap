package models

trait Password {
  def neverLog[A](f: String => A): A

  // intended to be final so you can never log a Password by inadvertance but by passing by neverLog
  final override def toString() = "[PROTECTED]"
}

object Password {
  def apply(password: String) = new Password {
    def neverLog[A](f: String => A): A = f(password)
  }
}
