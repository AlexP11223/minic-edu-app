package miniceduapp.helpers

import javafx.application.Platform
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyBooleanWrapper
import javafx.util.Duration
import java.util.*
import tornadofx.*

/**
 * Run the specified Runnable on the JavaFX Application Thread at some
 * unspecified time in the future.
 */
fun runLater(op: () -> Unit) = Platform.runLater(op)

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
    val runningProperty: ReadOnlyBooleanProperty get() = internalRunning.readOnlyProperty
    val running: Boolean get() = runningProperty.value

    private val internalCompleted = ReadOnlyBooleanWrapper(false)
    val completedProperty: ReadOnlyBooleanProperty get() = internalCompleted.readOnlyProperty
    val completed: Boolean get() = completedProperty.value

    override fun run() {
        internalRunning.value = true
        Platform.runLater {
            try {
                op()
            } finally {
                internalRunning.value = false
                internalCompleted.value = true
            }
        }
    }
}
