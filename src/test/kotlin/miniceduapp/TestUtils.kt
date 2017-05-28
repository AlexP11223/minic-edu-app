package miniceduapp

import com.sun.javafx.tk.Toolkit
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import miniceduapp.helpers.runLater
import tornadofx.*

/**
 * Wait on the UI thread until a certain value is available on this observable.
 *
 * This method does not block the UI thread even though it halts further execution until the condition is met.
 */
fun <T> ObservableValue<T>.awaitUntil(condition: (T) -> Boolean) {
    if (!Toolkit.getToolkit().canStartNestedEventLoop()) {
        throw IllegalStateException("awaitUntil is not allowed during animation or layout processing")
    }

    val changeListener = object : ChangeListener<T> {
        override fun changed(observable: ObservableValue<out T>?, oldValue: T, newValue: T) {
            if (condition(value)) {
                runLater {
                    Toolkit.getToolkit().exitNestedEventLoop(this@awaitUntil, null)
                    removeListener(this)
                }
            }
        }
    }

    runLater(100.millis) {
        changeListener.changed(this, value, value)
    }

    addListener(changeListener)
    Toolkit.getToolkit().enterNestedEventLoop(this)
}

/**
 * Wait on the UI thread until this observable value is true.
 *
 * This method does not block the UI thread even though it halts further execution until the condition is met.
 */
fun ObservableValue<Boolean>.awaitUntil() {
    this.awaitUntil { it }
}
