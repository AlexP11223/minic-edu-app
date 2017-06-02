package miniceduapp.viewmodels

import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.ReadOnlyStringWrapper
import javafx.beans.property.SimpleStringProperty
import javafx.util.Duration
import minic.Compiler
import miniceduapp.views.events.ErrorEvent
import tornadofx.*

class BytecodeViewModel(val updateDelay: Duration = 1.seconds) : ViewModel() {
    val mainViewModel: MainViewModel by inject()

    val status = TaskStatus()

    private val _programCodeProperty = ReadOnlyStringWrapper("")
    private var _programCode by _programCodeProperty
    val programCodeProperty: ReadOnlyStringProperty get() = _programCodeProperty.readOnlyProperty
    val programCode: String get() = _programCodeProperty.value

    val bytecodeTextProperty = SimpleStringProperty("")
    var bytecodeText by bytecodeTextProperty


    private var timerTask: FXTimerTask? = null

    init {
        mainViewModel.programCodeProperty.onChange {
            timerTask?.cancel()

            timerTask = runLater(updateDelay) {
                loadBytecode()
            }
        }
    }

    fun loadBytecode() {
        timerTask?.cancel()

        val code = mainViewModel.programCode

        if (code == programCode) {
            return
        }

        runAsync(status) {
            Compiler(code).bytecodeText()
        } ui {
            _programCode = code
            bytecodeText = it

        } fail {
            fire(ErrorEvent(it))
        }
    }
}
