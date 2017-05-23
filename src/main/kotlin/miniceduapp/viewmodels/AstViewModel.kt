package miniceduapp.viewmodels

import javafx.beans.property.SimpleObjectProperty
import javafx.embed.swing.SwingFXUtils
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.image.Image
import minic.Compiler
import miniceduapp.helpers.messageOrString
import tornadofx.*

class AstViewModel : ViewModel() {
    val mainViewModel: MainViewModel by inject()

    val status = TaskStatus()

    val astImageProperty = SimpleObjectProperty<Image>()
    var astImage by astImageProperty

    init {
        mainViewModel.programCodeProperty.onChange {
            loadAst()
        }
    }

    fun loadAst() {
        runAsync(status) {
            SwingFXUtils.toFXImage(Compiler(mainViewModel.programCode).drawAst(), null)
        } ui {
            astImage = it
        } fail {
            alert(Alert.AlertType.ERROR, "Error", it.messageOrString(), ButtonType.OK)
        }
    }

}
