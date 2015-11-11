package de.entera.riker

import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runner.Runner
import org.junit.runner.notification.RunNotifier
import java.io.Serializable

class ConcreteTest : AbstractTest({
    println("in 0 suite")

    suite("1.0 suite") {
        println("in 1.0 suite")
        var value = 0

        suiteSetup("1.1 suiteSetup") {
            println("in 1.1 suiteSetup")
            value++
        }

        test("1.2 test") {
            println("in 1.2 test ($value)")
            //assertEquals(11, value)
        }

        test("1.3 test") {
            println("in 1.3 test ($value)")
            //assertEquals(11, value)
        }

        value = 10
    }
})

//class ConcreteTestExt : AbstractTest({
//    run {}.notify("foo").run {}
//}

@RunWith(AbstractTestRunner::class)
abstract class AbstractTest {
    private val context: Context<TestBuilder>

    constructor(builder: TestBuilder.() -> Unit) {
        //TestBuilderImpl().builder()
        context = Context(builder)
        context.invokeOn(TestBuilderImpl())
    }
}

interface TestBuilder {
    fun suite(text: String,
              builder: TestBuilder.() -> Unit)

    fun suiteSetup(text: String = "",
                   action: () -> Unit)

    fun test(text: String,
             action: () -> Unit)
}

private fun <T> invokeOn(receiver: T,
                         method: T.() -> Unit) {
    receiver.method()
}

private class Context<T>(val method: T.() -> Unit) {
    fun invokeOn(receiver: T) = invokeOn(receiver, method)
}

private class TestBuilderImpl : TestBuilder {
    private var suiteSetupContext: Context<() -> Unit>? = null

    override fun suite(text: String,
                       builder: TestBuilder.() -> Unit) {
        println(text)
        builder()
    }

    override fun suiteSetup(text: String,
                            action: () -> Unit) {
        suiteSetupContext = Context({ action() })
    }

    override fun test(text: String,
                      action: () -> Unit) {
        println(text)
        suiteSetupContext!!.invokeOn({})
        action()
    }

    fun run(function: () -> Unit): TestBuilder { return this }
    fun notify(label: String): TestBuilder { return this }
}

//class AbstractSpecRunner<T>(val specClass: Class<T>) : BlockJUnit4ClassRunner(specClass) {}

private class AbstractTestRunner<T>(val testClass: Class<T>) : Runner() {
    override fun getDescription(): Description? {
        val suiteDescription = Description.createSuiteDescription(testClass)
        //return suiteDescription.childlessCopy()
        val testDescription = Description.createTestDescription(testClass, "foo")
        suiteDescription.addChild(testDescription)
        return suiteDescription
    }

    override fun run(notifier: RunNotifier) {
        val test: T = testClass.newInstance()

        notifier.fireTestStarted(Description.createTestDescription(testClass, "foo"))
        Thread.sleep(1000)
        notifier.fireTestFinished(Description.createTestDescription(testClass, "foo"))

        notifier.fireTestStarted(Description.createTestDescription(testClass, "bar"))
        Thread.sleep(1000)
        notifier.fireTestFinished(Description.createTestDescription(testClass, "bar"))
    }
}

private data class UniqueId(val id: Int) : Serializable {
    companion object {
        var currentId = 0; private set
        fun nextId() = UniqueId(currentId++)
    }
}
