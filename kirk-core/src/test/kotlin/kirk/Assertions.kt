package kirk

import kirk.api.Assertion
import kirk.api.expect
import kirk.assertions.*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.*
import java.time.LocalDate
import kotlin.test.assertEquals

internal object Assertions : Spek({

  describe("assertions on ${Any::class.simpleName}") {
    describe("isNull assertion") {
      it("passes if the subject is null") {
        val subject: Any? = null
        expect(subject).isNull()
      }
      it("fails if the subject is not null") {
        fails {
          val subject: Any? = "covfefe"
          expect(subject).isNull()
        }
      }
      @Suppress("USELESS_IS_CHECK")
      it("down-casts the result") {
        val subject: Any? = null
        expect(subject)
          .also { assert(it is Assertion<Any?>) }
          .isNull()
          .also { assert(it is Assertion<Nothing>) }
      }
    }

    describe("isNotNull assertion") {
      it("fails if the subject is null") {
        fails {
          val subject: Any? = null
          expect(subject).isNotNull()
        }
      }
      it("passes if the subject is not null") {
        val subject: Any? = "covfefe"
        expect(subject).isNotNull()
      }
      @Suppress("USELESS_IS_CHECK")
      it("down-casts the result") {
        val subject: Any? = "covfefe"
        expect(subject)
          .also { assert(it is Assertion<Any?>) }
          .isNotNull()
          .also { assert(it is Assertion<Any>) }
      }
    }

    describe("isA assertion") {
      it("fails if the subject is null") {
        fails {
          val subject: Any? = null
          expect(subject).isA<String>()
        }
      }
      it("fails if the subject is a different type") {
        fails {
          val subject = 1L
          expect(subject).isA<String>()
        }
      }
      it("passes if the subject is the same exact type") {
        val subject = "covfefe"
        expect(subject).isA<String>()
      }
      it("passes if the subject is a sub-type") {
        val subject: Any = 1L
        expect(subject).isA<Number>()
      }
      @Suppress("USELESS_IS_CHECK")
      it("down-casts the result") {
        val subject: Any = 1L
        expect(subject)
          .also { assert(it is Assertion<Any>) }
          .isA<Number>()
          .also { assert(it is Assertion<Number>) }
          .isA<Long>()
          .also { assert(it is Assertion<Long>) }
      }
      @Suppress("USELESS_IS_CHECK")
      it("allows specialized assertions after establishing type") {
        val subject: Any = "covfefe"
        expect(subject)
          .also { assert(it is Assertion<Any>) }
          .isA<String>()
          .also { assert(it is Assertion<String>) }
          .hasLength(7) // only available on Assertion<CharSequence>
      }
    }

    describe("isEqualTo assertion") {
      it("passes if the subject matches the expectation") {
        expect("covfefe").isEqualTo("covfefe")
      }
      it("fails if the subject does not match the expectation") {
        fails {
          expect("covfefe").isEqualTo("COVFEFE")
        }
      }
      it("fails if the subject is a different type to the expectation") {
        fails {
          expect(1).isEqualTo(1L)
        }
      }
      it("can be used on a null subject") {
        fails {
          expect(null).isEqualTo("covfefe")
        }
      }
      it("can be used with a null expected value") {
        fails {
          expect("covfefe").isEqualTo(null)
        }
      }
    }

    describe("isNotEqualTo assertion") {
      it("fails if the subject matches the expectation") {
        fails {
          expect("covfefe").isNotEqualTo("covfefe")
        }
      }
      it("passes if the subject does not match the expectation") {
        expect("covfefe").isNotEqualTo("COVFEFE")
      }
      it("passes if the subject is a different type to the expectation") {
        expect(1).isNotEqualTo(1L)
      }
      it("can be used on a null subject") {
        expect(null).isNotEqualTo("covfefe")
      }
      it("can be used with a null expected value") {
        expect("covfefe").isNotEqualTo(null)
      }
    }
  }

  describe("assertions on ${CharSequence::class.simpleName}") {
    describe("hasLength assertion") {
      it("passes if the subject has the expected length") {
        expect("covfefe").hasLength(7)
      }
      it("fails if the subject does not have the expected length") {
        fails {
          expect("covfefe").hasLength(1)
        }
      }
    }

    describe("matches assertion") {
      it("passes if the subject is a full match for the regex") {
        expect("covfefe").matches("[cefov]+".toRegex())
      }
      it("fails if the subject is only a partial match for the regex") {
        fails {
          expect("despite the negative press covfefe").matches("[cefov]+".toRegex())
        }
      }
      it("fails if the subject does not match the regex") {
        fails {
          expect("covfefe").matches("\\d+".toRegex())
        }
      }
    }
  }

  describe("assertions on ${Collection::class.simpleName}") {
    describe("hasSize assertion") {
      it("fails if the subject size is not the expected size") {
        fails {
          val subject = setOf("catflap", "rubberplant", "marzipan")
          expect(subject).hasSize(1)
        }
      }

      describe("isEmpty assertion") {
        it("passes if collection is empty") {
          expect(emptyList<Any>()).isEmpty()
        }
        it("fails if the collection is not empty") {
          fails {
            expect(listOf("catflap", "rubberplant", "marzipan")).isEmpty()
          }
        }
      }

      describe("isNotEmpty assertion") {
        it("fails if collection is empty") {
          fails {
            expect(emptyList<Any>()).isNotEmpty()
          }
        }
        it("passes if the collection is not empty") {
          expect(listOf("catflap", "rubberplant", "marzipan")).isNotEmpty()
        }
      }
    }
  }

  describe("assertions on ${Iterable::class.simpleName}") {
    describe("all assertion") {
      it("passes if all elements conform") {
        val subject = setOf("catflap", "rubberplant", "marzipan")
        expect(subject).all {
          isLowerCase()
        }
      }
      it("fails if any element does not conform") {
        fails {
          val subject = setOf("catflap", "rubberplant", "marzipan")
          expect(subject).all {
            startsWith('c')
          }
        }
          .let { failure ->
            assertEquals(3, failure.assertionCount, "Assertions")
            assertEquals(1, failure.passCount, "Passed")
            assertEquals(2, failure.failureCount, "Failed")
          }
      }
    }

    describe("any assertion") {
      it("passes if all elements conform") {
        val subject = setOf("catflap", "rubberplant", "marzipan")
        expect(subject).any {
          isLowerCase()
        }
      }
      it("passes if any one element conforms") {
        val subject = setOf("catflap", "RUBBERPLANT", "MARZIPAN")
        expect(subject).any {
          isLowerCase()
        }
      }
      it("fails if no elements conform") {
        fails {
          val subject = setOf("CATFLAP", "RUBBERPLANT", "MARZIPAN")
          expect(subject).any {
            isLowerCase()
          }
        }
          .let { failure ->
            assertEquals(3, failure.assertionCount, "Assertions")
            assertEquals(0, failure.passCount, "Passed")
            assertEquals(3, failure.failureCount, "Failed")
          }
      }
    }

    describe("none assertion") {
      it("passes if no elements conform") {
        val subject = setOf("catflap", "rubberplant", "marzipan")
        expect(subject).none {
          isUpperCase()
        }
      }
      it("fails if some elements conforms") {
        fails {
          val subject = setOf("catflap", "RUBBERPLANT", "MARZIPAN")
          expect(subject).none {
            isUpperCase()
          }
        }
          .let { failure ->
            assertEquals(3, failure.assertionCount, "Assertions")
            assertEquals(2, failure.passCount, "Passed")
            assertEquals(1, failure.failureCount, "Failed")
          }
      }
      it("fails if all elements conform") {
        fails {
          val subject = setOf("CATFLAP", "RUBBERPLANT", "MARZIPAN")
          expect(subject).none {
            isUpperCase()
          }
        }
          .let { failure ->
            assertEquals(3, failure.assertionCount, "Assertions")
            assertEquals(3, failure.passCount, "Passed")
            assertEquals(0, failure.failureCount, "Failed")
          }
      }
    }

    describe("contains assertion") {
      it("passes if the expected element is the only actual element") {
        expect(listOf("catflap")).contains("catflap")
      }

      it("passes if the expected element is one of the actual elements") {
        expect(listOf("catflap", "rubberplant", "marzipan")).contains("catflap")
      }

      it("passes if the expected elements are among the actual elements") {
        expect(listOf("catflap", "rubberplant", "marzipan")).contains("catflap", "marzipan")
      }

      it("fails if the expected element is not present") {
        fails {
          expect(listOf("catflap", "rubberplant", "marzipan")).contains("covfefe")
        }
      }

      it("has a nested failure for each missing element") {
        fails {
          expect(listOf("catflap", "rubberplant", "marzipan")).contains("covfefe", "marzipan", "bojack")
        }.let { e ->
          assertEquals(3, e.assertionCount, "Assertions")
          assertEquals(1, e.passCount, "Passed")
          assertEquals(2, e.failureCount, "Failed")
        }
      }

      it("fails if no expected elements are supplied") {
        fails {
          expect(listOf("catflap", "rubberplant", "marzipan")).contains()
        }
      }

      it("fails if subject is empty") {
        fails {
          expect(emptyList<String>()).contains("catflap")
        }
      }
    }
  }

  describe("assertions on ${Comparable::class.simpleName}") {
    describe("isGreaterThan assertion") {
      it("passes if the subject is greater than the expected value") {
        expect(1).isGreaterThan(0)
      }
      it("fails if the subject is equal to the expected value") {
        fails {
          expect(1).isGreaterThan(1)
        }
      }
      it("fails if the subject is less than the expected value") {
        fails {
          expect(LocalDate.of(2018, 5, 1)).isGreaterThan(LocalDate.of(2018, 5, 2))
        }
      }
    }

    describe("isLessThan assertion") {
      it("passes if the subject is less than the expected value") {
        expect(0).isLessThan(1)
      }
      it("fails if the subject is equal to the expected value") {
        fails {
          expect(1).isLessThan(1)
        }
      }
      it("fails if the subject is greater than the expected value") {
        fails {
          expect(LocalDate.of(2018, 5, 2)).isLessThan(LocalDate.of(2018, 5, 1))
        }
      }
    }
    describe("isGreaterThanOrEqualTo assertion") {
      it("passes if the subject is greater than the expected value") {
        expect(1).isGreaterThanOrEqualTo(0)
      }
      it("passes if the subject is equal to the expected value") {
        expect(1).isGreaterThanOrEqualTo(1)
      }
      it("fails if the subject is less than the expected value") {
        fails {
          expect(LocalDate.of(2018, 5, 1)).isGreaterThanOrEqualTo(LocalDate.of(2018, 5, 2))
        }
      }
    }

    describe("isLessThanOrEqualTo assertion") {
      it("passes if the subject is less than the expected value") {
        expect(0).isLessThanOrEqualTo(1)
      }
      it("passes if the subject is equal to the expected value") {
        expect(1).isLessThanOrEqualTo(1)
      }
      it("fails if the subject is greater than the expected value") {
        fails {
          expect(LocalDate.of(2018, 5, 2)).isLessThanOrEqualTo(LocalDate.of(2018, 5, 1))
        }
      }
    }
  }
})
