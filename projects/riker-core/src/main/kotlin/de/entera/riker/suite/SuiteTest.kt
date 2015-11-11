package de.entera.riker.suite

import org.junit.Test
import kotlin.test.expect

class SuiteTest {
    @Test
    fun `transform() test`() {
        // given:
        val source = Node("suite",
            Node("test")
        )
        val expected = Node("suite",
            Node("testCase",
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

    val testCases = node.children.filter { it.data == "test" }
                                 .map { Node("testCase", it ) }
    target.children.addAll(testCases)

    return target
}
