package miniceduapp

import miniceduapp.views.events.ErrorEvent
import miniceduapp.views.events.ErrorMessageEvent
import org.junit.After
import tornadofx.*
import java.util.*

open class BaseTornadoFxComponentTest : Component() {
    private var ignoreErrors = false
    private var finished = false // should unsubscribe instead of this

    private val errors = Collections.synchronizedCollection(mutableListOf<Throwable>())

    init {
        subscribe<ErrorEvent> {
            if (!ignoreErrors && !finished) {
                errors.add(it.error)
                throw it.error
            }
        }
        subscribe<ErrorMessageEvent> {
            if (!ignoreErrors && !finished) {
                errors.add(Exception(it.text))
                throw Exception(it.text)
            }
        }
    }

    @After
    fun afterTest() {
        finished = true

        if (errors.any()) {
            throw errors.first()
        }
    }

    protected fun ignoreErrors(op: () -> Unit) {
        ignoreErrors = true
        try {
            op()
        } finally {
            ignoreErrors = false
        }
    }
}
