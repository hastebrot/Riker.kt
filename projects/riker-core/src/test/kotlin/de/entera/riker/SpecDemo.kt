package de.entera.riker

import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals

fun sum(a: Int, b: Int) = a + b

class CalcSpec: Spek() { init {
    given("foo") {
        on("bar") {
            val result = sum(1, 2)

            it("baz") {
                assertEquals(4, result)
            }

            it("baz") {
                assertEquals(3, result)
            }
        }
    }
}}
