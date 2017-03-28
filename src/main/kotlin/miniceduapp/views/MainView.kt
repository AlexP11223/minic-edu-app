package miniceduapp.views

import miniceduapp.Styles
import javafx.scene.control.Alert.AlertType.INFORMATION
import tornadofx.*

class MainView : View("Hello") {
    override val root = borderpane {
        addClass(Styles.welcomeScreen)
        top {
            stackpane {
                label(title).addClass(Styles.heading)
            }
        }
        center {
            stackpane {
                addClass(Styles.content)
                button("Click me") {
                    setOnAction {
                        alert(INFORMATION, "Hello", "")
                    }
                }
            }
        }
    }
}

