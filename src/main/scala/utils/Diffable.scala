package utils

// // Usage
// case class Person(name: String, age: Int)
// val p1 = Person("Alice", 30)
// val p2 = Person("Alice", 35)

// val differences = Diffable.diff(p1, p2)
// println(differences) // Output: Map("age" -> (30, 35))


trait Diffable[T] {
  def diff(a: T, b: T): Map[String, (Any, Any)]
}

object Diffable {
  def apply[T](implicit instance: Diffable[T]): Diffable[T] = instance

  // Generic Implementation for Any Case Class
  implicit def caseClassDiffable[T <: Product]: Diffable[T] = (a: T, b: T) => {
    val fields = a.getClass.getDeclaredFields.map(_.getName)
    val valuesA = a.productIterator.toList
    val valuesB = b.productIterator.toList

    fields
      .zip(valuesA.zip(valuesB))
      .collect {
        case (field, (valA, valB)) if valA != valB => field -> (valA, valB)
      }
      .toMap
  }

  // Helper method to compare two instances
  def diff[T](a: T, b: T)(implicit diffInstance: Diffable[T]): Map[String, (Any, Any)] =
    diffInstance.diff(a, b)
}

