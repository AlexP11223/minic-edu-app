package miniceduapp.helpers

import javafx.stage.Stage
import tornadofx.*

fun View.setMinimumWindowSize(minWidth: Double, minHeight: Double) {
    val window = (root.scene.window as? Stage) // or (currentWindow as? Stage) if it works
    if (window == null) {
        FX.log.warning("window not found for $this")
    }
    window?.minWidth = minWidth
    window?.minHeight = minHeight
}

fun View.setMinimumWindowSize(minWidth: Int, minHeight: Int) = setMinimumWindowSize(minWidth.toDouble(), minHeight.toDouble())
