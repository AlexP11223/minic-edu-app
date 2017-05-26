package miniceduapp.helpers

import javafx.application.Platform
import javafx.beans.property.ReadOnlyBooleanWrapper
import javafx.util.Duration
import java.util.*
import tornadofx.*

/**
 * Run the specified Runnable on the JavaFX Application Thread after a
 * specified delay.
 *
 * runLater(10.seconds) {
 *     // Do something on the application thread
 * }
 *
 * This function returns a TimerTask. You can cancel the task before the time
 * is up to abort the execution.
 */
fun runLater(delay: Duration, op: () -> Unit): FXTimerTask {
    val timer = Timer(true)
    val task = FXTimerTask(op, timer)
    timer.schedule(task, delay.toMillis().toLong())
    return task
}

class FXTimerTask(val op: () -> Unit, val timer: Timer) : TimerTask() {
    private val internalRunning = ReadOnlyBooleanWrapper(false)
    val runningProperty = internalRunning.readOnlyProperty
    val running by runningProperty

    override fun run() {
        internalRunning.value = true
        try {
            Platform.runLater(op)
        } finally {
            internalRunning.value = false
        }
    }
}
