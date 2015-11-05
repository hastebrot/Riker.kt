package de.entera.riker

import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runner.Runner
import org.junit.runner.notification.RunNotifier
import java.io.Serializable

class ConcreteSpec : AbstractSpec({
    describe("define describe") {
        beforeEach {
            println("in before")
        }

        it("define foo") {
            println("in foo")
        }

        it("define bar") {
            println("in bar")
        }
    }

    // run {}.notify("foo").run {}
})

@RunWith(AbstractSpecRunner::class)
abstract class AbstractSpec {
    constructor(definition: TestDefinition.() -> Unit) {
        TestDefinition().definition()
    }

    fun run(function: () -> Unit): AbstractSpec { return this }
    fun notify(label: String): AbstractSpec { return this }
}

class TestDefinition() {
    private class Context<T>(val action: T.() -> Unit) {
        fun run(receiver: T) = receiver.action()
    }

    private var beforeEachContext: Context<TestDefinition>? = null

    fun describe(text: String, action: TestDefinition.() -> Unit) {
        println(text)
        action()
    }

    fun beforeEach(action: TestDefinition.() -> Unit) {
        beforeEachContext = Context(action)
    }

    fun it(text: String, action: TestDefinition.() -> Unit) {
        println(text)
        beforeEachContext!!.run(TestDefinition())
        action()
    }
}

//class AbstractSpecRunner<T>(val specClass: Class<T>) : BlockJUnit4ClassRunner(specClass) {}

data class JUnitUniqueId(val id: Int) : Serializable {
    companion object {
        var id = 0
        fun next() = JUnitUniqueId(id++)
    }
}

class AbstractSpecRunner<T>(val specClass: Class<T>) : Runner() {
    override fun getDescription(): Description {
        val suiteDescription = Description.createSuiteDescription(specClass)
        val testDescription = Description.createTestDescription(specClass, "foo")
        suiteDescription.addChild(testDescription)
        return suiteDescription
    }

    override fun run(notifier: RunNotifier) {
        specClass.newInstance()
        notifier.fireTestStarted(Description.createTestDescription(specClass, "foo"))
        Thread.sleep(1000)
        notifier.fireTestFinished(Description.createTestDescription(specClass, "foo"))
    }
}
