package miniceduapp.viewmodels

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class MainViewModel : ViewModel() {

    val programCodeProperty = SimpleStringProperty()
    var programCode by programCodeProperty
}
