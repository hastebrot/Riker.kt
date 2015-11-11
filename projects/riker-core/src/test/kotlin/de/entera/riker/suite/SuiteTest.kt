package de.entera.riker.suite

import org.junit.Test
import kotlin.test.expect

class SuiteTest {
    @Test
    fun `transform() test test`() {
        // given:
        val source = Node("suite",
            Node("test"),
            Node("test")
        )
        val expected = Node("suite",
            Node("testCase",
                Node("test")
            ),
            Node("testCase",
                Node("test")
            )
        )

        // expect:
        expect(expected) { transform(source) }
    }

    @Test
    fun `transform() testSetup test test`() {
        // given:
        val source = Node("suite",
            Node("testSetup"),
            Node("test"),
            Node("test")
        )
        val expected = Node("suite",
            Node("testCase",
                Node("testSetup"),
                Node("test")
            ),
            Node("testCase",
                Node("testSetup"),
                Node("test")
            )
        )

        // expect:
        expect(expected) { transform(source) }
    }
}

data class Node<T>(val data: T,
                   val children: MutableList<Node<T>> = arrayListOf()) {
    constructor(data: T,
                vararg children: Node<T>) : this(data) {
        this.children.addAll(children)
    }
}

fun transform(node: Node<String>): Node<String> {
    val target = Node(node.data)

    // collect setups and cleanups.
    val testSetups = node.children.filter { it.data == "testSetup" }
    val testCleanups = node.children.filter { it.data == "testCleanup" }

    // populate test cases.
    val testCases = node.children.filter { it.data == "test" }
        .map { Node("testCase", (testSetups + it + testCleanups).toArrayList() ) }
    target.children.addAll(testCases)

    return target
}
