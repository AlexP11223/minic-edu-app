package miniceduapp

import miniceduapp.views.events.ErrorEvent
import tornadofx.*

open class BaseTornadoFxComponentTest : Component() {
    init {
        subscribe<ErrorEvent> { throw it.error }
    }
}
