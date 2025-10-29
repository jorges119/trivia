import zio._
import zio.test.{test, _}
import zio.test.Assertion._

import HelloWorld._

object HelloWorld {
  def sayHello: ZIO[Any, Throwable, Unit] =
    Console.printLine("Hello, World!")
}

object HelloWorldSpec extends ZIOSpecDefault {
  def spec = suite("HelloWorldSpec")(
    suite("ReverseSpec") {
      // ∀ xs. reverse(reverse(xs)) == xs
      test("reversing a list twice must give the original list")(
        check(Gen.listOf(Gen.int)) { list =>
          assertTrue(reverse(reverse(list)) == list)
        }
      )
    },
    suite("Simple") {
      test("sayHello correctly displays output") {
        for {
          _      <- sayHello
          output <- TestConsole.output
        } yield assertTrue(output == Vector("Hello, World!\n"))
      }
    }
  )
}

def reverse[T](list: List[T]): List[T] =
  if (list.length > 6) list.reverse.dropRight(1) else list.reverse
