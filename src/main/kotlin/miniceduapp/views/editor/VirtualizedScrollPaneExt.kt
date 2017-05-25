package miniceduapp.views.editor


import javafx.css.PseudoClass
import javafx.geometry.Insets
import javafx.scene.Node
import org.fxmisc.flowless.Virtualized
import org.fxmisc.flowless.VirtualizedScrollPane

class VirtualizedScrollPaneExt<T>(content: T) : VirtualizedScrollPane<T>(content) where T : Node, T : Virtualized {
    private val FOCUSED = PseudoClass.getPseudoClass("focused")

    init {

        padding = Insets(10.0, 20.0, 10.0, 20.0)
        content.focusedProperty().addListener { _, _, newVal -> pseudoClassStateChanged(FOCUSED, newVal!!) }
    }
}
