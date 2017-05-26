package miniceduapp

import javafx.util.Duration
import miniceduapp.views.events.ErrorEvent
import org.awaitility.Awaitility
import org.testfx.api.FxToolkit
import tornadofx.*
import java.util.concurrent.TimeUnit

open class BaseTornadoFxComponentTest : Component() {
    init {
        FxToolkit.registerPrimaryStage()
        subscribe<ErrorEvent> { throw it.error }
    }

    fun waitForUi(timeout: Duration = 10.seconds, condition: () -> Boolean) {
        Awaitility.await()
                .atMost(timeout.toMillis().toLong(), TimeUnit.MILLISECONDS)
                .until(condition)
    }
}
