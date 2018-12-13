package strikt.assertions

import com.oneeyedmen.minutest.junit.testFactoryFor
import com.oneeyedmen.minutest.rootContext
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import strikt.api.expectThat

@DisplayName("assertions on Boolean")
@Suppress("SimplifyBooleanWithConstants")
internal object BooleanAssertions {
  @TestFactory
  fun isTrue() = testFactoryFor(rootContext<Unit> {
    test("passes when the subject is true") {
      expectThat("a" == "a").isTrue()
    }

    test("fails when the subject is false") {
      assertThrows<AssertionError> {
        expectThat("a" == "A").isTrue()
      }
    }

    test("fails when the subject is null") {
      assertThrows<AssertionError> {
        expectThat(null).isTrue()
      }
    }
  })

  @TestFactory
  fun isFalse() = testFactoryFor(rootContext<Unit> {
    test("passes when the subject is false") {
      expectThat("a" == "A").isFalse()
    }

    test("fails when the subject is false") {
      assertThrows<AssertionError> {
        expectThat("a" == "a").isFalse()
      }
    }

    test("fails when the subject is null") {
      assertThrows<AssertionError> {
        expectThat(null).isFalse()
      }
    }
  })
}
