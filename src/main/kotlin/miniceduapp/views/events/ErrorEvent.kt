package miniceduapp.views.events

import tornadofx.*

class ErrorEvent(val error: Throwable) : FXEvent()
