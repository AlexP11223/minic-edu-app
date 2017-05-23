package miniceduapp.views

import javafx.scene.control.Slider
import javafx.scene.image.ImageView
import javafx.scene.layout.Priority
import miniceduapp.viewmodels.AstViewModel
import miniceduapp.views.styles.Styles
import tornadofx.*
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class AstView : View("AST") {

    val viewModel: AstViewModel by inject()

    var img: ImageView by singleAssign()
    var zoomSlider: Slider by singleAssign()

    override val root = hbox {
        minWidth = 1000.0
        minHeight = 700.0
        vbox {
            stackpane {
                scrollpane {
                    img = imageview {
                        preserveRatioProperty().set(true)
                        imageProperty().bind(viewModel.astImageProperty)
                    }
                    hgrow = Priority.ALWAYS
                    vgrow = Priority.ALWAYS
                    minWidth = 1000.0
                    minHeight = 700.0
                    addClass(Styles.whitePanel)
                }
                imageview("loading.gif") {
                    visibleWhen { viewModel.status.running }
                }
            }
            hbox {
                zoomSlider = slider(10.0, 600.0) {
                    hgrow = Priority.ALWAYS
                    valueProperty().onChange { value ->
                        val ratio = value / 100
                        img.scaleX = ratio
                        img.scaleY = ratio
                    }
                }
                label {
                    bind(zoomSlider.valueProperty(), format = DecimalFormat("#", DecimalFormatSymbols.getInstance(Locale.ENGLISH)))
                }
                label("%")
            }
        }
        style {
            padding = box(10.px)
        }
    }

    override fun onDock() {
        viewModel.loadAst()
    }
}
