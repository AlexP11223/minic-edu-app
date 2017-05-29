package miniceduapp.viewmodels

import javafx.beans.property.SimpleObjectProperty
import javafx.embed.swing.SwingFXUtils
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.image.Image
import javafx.util.Duration
import minic.Compiler
import minic.frontend.ast.Program
import miniceduapp.helpers.messageOrString
import miniceduapp.views.events.ErrorEvent
import tornadofx.*

class AstViewModel(val updateDelay: Duration = 2.seconds) : ViewModel() {
    val mainViewModel: MainViewModel by inject()

    val status = TaskStatus()

    val astImageProperty = SimpleObjectProperty<Image>()
    var astImage: Image by astImageProperty

    val astProperty = SimpleObjectProperty<Program>()
    var ast: Program by astProperty

    val programCodeProperty = bind { mainViewModel.programCodeProperty }

    init {
        mainViewModel.programCodeProperty.onChange {
            loadAst()
        }
    }

    fun loadAst() {
        runAsync(status) {
            val compiler = Compiler(mainViewModel.programCode)
            SwingFXUtils.toFXImage(compiler.drawAst(), null) to compiler.ast
        } ui {
            astImage = it.first
            ast = it.second
        } fail {
            fire(ErrorEvent(it))
        }
    }

}
