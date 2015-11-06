package de.entera.riker

// The BDD interface provides describe(), context(), it(), before(), after(), beforeEach(), and
// afterEach(). context() is just an alias for describe(), and behaves the same way; it just
// provides a way to keep tests easier to read and organized.
//
// The TDD interface provides suite(), test(), suiteSetup(), suiteTeardown(), setup(), and
// teardown().
//
// -- the mocha.js documentation

import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runner.Runner
import org.junit.runner.notification.RunNotifier
import java.io.Serializable

class ConcreteTest : AbstractTest({
    suite("define suite") {
        println("in suite")

        suiteSetup {
            println("in suiteSetup")
        }

        test("define foo test") {
            println("in foo test")
        }

        test("define bar test") {
            println("in bar test")
        }
    }

    //run {}.notify("foo").run {}
})

@RunWith(AbstractTestRunner::class)
abstract class AbstractTest {
    private val context: Context<TestBuilder>

    constructor(builder: TestBuilder.() -> Unit) {
        context = Context(builder)
        context.invokeOn(TestBuilderImpl())
    }
}

interface TestBuilder {
    fun suite(text: String,
              action: TestBuilder.() -> Unit)

    fun suiteSetup(action: TestBuilder.() -> Unit)

    fun test(text: String,
             action: TestBuilder.() -> Unit)
}

private fun <T> invokeOn(receiver: T,
                         action: T.() -> Unit) {
    receiver.action()
}

private class Context<T>(val action: T.() -> Unit) {
    fun invokeOn(receiver: T) = invokeOn(receiver, action)
}

private class TestBuilderImpl : TestBuilder {
    private var suiteSetupContext: Context<TestBuilder>? = null

    override fun suite(text: String,
                       action: TestBuilder.() -> Unit) {
        println(text)
        action()
    }

    override fun suiteSetup(action: TestBuilder.() -> Unit) {
        suiteSetupContext = Context(action)
    }

    override fun test(text: String,
                      action: TestBuilder.() -> Unit) {
        println(text)
        suiteSetupContext!!.invokeOn(this)
        action()
    }

    fun run(function: () -> Unit): TestBuilder { return this }
    fun notify(label: String): TestBuilder { return this }
}

//class AbstractSpecRunner<T>(val specClass: Class<T>) : BlockJUnit4ClassRunner(specClass) {}

private class AbstractTestRunner<T>(val testClass: Class<T>) : Runner() {
    override fun getDescription(): Description {
        val suiteDescription = Description.createSuiteDescription(testClass)
        val testDescription = Description.createTestDescription(testClass, "foo")
        suiteDescription.addChild(testDescription)
        return suiteDescription
    }

    override fun run(notifier: RunNotifier) {
        val test: T = testClass.newInstance()

        notifier.fireTestStarted(Description.createTestDescription(testClass, "foo"))
        Thread.sleep(1000)
        notifier.fireTestFinished(Description.createTestDescription(testClass, "foo"))
    }
}

private data class UniqueId(val id: Int) : Serializable {
    companion object {
        var id = 0
        fun generateId() = UniqueId(id++)
    }
}
