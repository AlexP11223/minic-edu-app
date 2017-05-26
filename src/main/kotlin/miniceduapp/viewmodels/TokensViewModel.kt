package miniceduapp.viewmodels

import javafx.beans.property.SimpleStringProperty
import javafx.util.Duration
import minic.Compiler
import minic.frontend.lexer.Token
import miniceduapp.helpers.FXTimerTask
import miniceduapp.helpers.runLater
import miniceduapp.views.events.ErrorEvent
import tornadofx.*

class TokensViewModel(val updateDelay: Duration = 1.seconds) : ViewModel() {
    val mainViewModel: MainViewModel by inject()

    val status = TaskStatus()

    val programCodeProperty = SimpleStringProperty()
    var programCode: String by programCodeProperty

    val tokens = mutableListOf<Token>().observable()

    private var timerTask: FXTimerTask? = null

    init {
        mainViewModel.programCodeProperty.onChange {
            timerTask?.cancel()

            timerTask = runLater(updateDelay) {
                loadTokens()
            }
        }
    }

    fun loadTokens() {
        timerTask?.cancel()

        programCode = mainViewModel.programCode
        tokens.clear()

        val code = programCode // probably shouldn't access property from another thread

        runAsync(status) {
            Compiler(code).tokens
        } ui {
            tokens.clear()
            tokens.addAll(it)
        } fail {
            fire(ErrorEvent(it))
        }
    }
}
