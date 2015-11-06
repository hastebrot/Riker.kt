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

    //run {}.notify("foo").run {}
})

@RunWith(AbstractSpecRunner::class)
abstract class AbstractSpec {
    private val context: Context<SpecBuilder>

    constructor(builder: SpecBuilder.() -> Unit) {
        context = Context(builder)
        context.invokeOn(SpecBuilderImpl())
    }
}

interface SpecBuilder {
    fun describe(text: String,
                 action: SpecBuilder.() -> Unit)

    fun beforeEach(action: SpecBuilder.() -> Unit)

    fun it(text: String,
           action: SpecBuilder.() -> Unit)
}

private fun <T> invokeOn(receiver: T,
                         action: T.() -> Unit) {
    receiver.action()
}

private class Context<T>(val action: T.() -> Unit) {
    fun invokeOn(receiver: T) = invokeOn(receiver, action)
}

private class SpecBuilderImpl : SpecBuilder {
    private var beforeEachContext: Context<SpecBuilder>? = null

    override fun describe(text: String,
                          action: SpecBuilder.() -> Unit) {
        println(text)
        action()
    }

    override fun beforeEach(action: SpecBuilder.() -> Unit) {
        beforeEachContext = Context(action)
    }

    override fun it(text: String,
                    action: SpecBuilder.() -> Unit) {
        println(text)
        beforeEachContext!!.invokeOn(this)
        action()
    }

    fun run(function: () -> Unit): SpecBuilder { return this }
    fun notify(label: String): SpecBuilder { return this }
}

//class AbstractSpecRunner<T>(val specClass: Class<T>) : BlockJUnit4ClassRunner(specClass) {}

private class AbstractSpecRunner<T>(val specClass: Class<T>) : Runner() {
    override fun getDescription(): Description {
        val suiteDescription = Description.createSuiteDescription(specClass)
        val testDescription = Description.createTestDescription(specClass, "foo")
        suiteDescription.addChild(testDescription)
        return suiteDescription
    }

    override fun run(notifier: RunNotifier) {
        val spec: T = specClass.newInstance()

        notifier.fireTestStarted(Description.createTestDescription(specClass, "foo"))
        Thread.sleep(1000)
        notifier.fireTestFinished(Description.createTestDescription(specClass, "foo"))
    }
}

private data class JUnitUniqueId(val id: Int) : Serializable {
    companion object {
        var id = 0
        fun next() = JUnitUniqueId(id++)
    }
}
