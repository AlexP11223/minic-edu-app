package miniceduapp.viewmodels

import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.ReadOnlyStringWrapper
import javafx.beans.property.SimpleObjectProperty
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import javafx.util.Duration
import minic.Compiler
import minic.frontend.ast.Program
import miniceduapp.views.events.ErrorEvent
import tornadofx.*

class AstViewModel(val updateDelay: Duration = 2.seconds) : ViewModel() {
    val mainViewModel: MainViewModel by inject()

    val status = TaskStatus()

    val astImageProperty = SimpleObjectProperty<Image>()
    var astImage: Image by astImageProperty

    val astProperty = SimpleObjectProperty<Program>()
    var ast: Program by astProperty

    private val _programCodeProperty = ReadOnlyStringWrapper("")
    private var _programCode by _programCodeProperty
    val programCodeProperty: ReadOnlyStringProperty get() = _programCodeProperty.readOnlyProperty
    val programCode: String get() = _programCodeProperty.value

    private var timerTask: FXTimerTask? = null

    init {
        mainViewModel.programCodeProperty.onChange {
            timerTask?.cancel()

            timerTask = runLater(updateDelay) {
                loadAst()
            }
        }

        loadAst()
    }

    fun loadAst() {
        timerTask?.cancel()

        mainViewModel.validateCode()
        if (mainViewModel.hasParsingErrors) {
            return
        }

        val code = mainViewModel.programCode

        if (code == programCode || status.running.value) {
            return
        }

        runAsync(status) {
            val compiler = Compiler(mainViewModel.programCode)
            SwingFXUtils.toFXImage(compiler.drawAst(), null) to compiler.ast
        } ui {
            _programCode = code
            astImage = it.first
            ast = it.second
        } fail {
            fire(ErrorEvent(it))
        }
    }

}
