package miniceduapp.views

import javafx.event.EventTarget
import javafx.scene.control.TextArea
import miniceduapp.Styles
import miniceduapp.controllers.MainController
import tornadofx.*

class MainView : View("") {
    val controller: MainController by inject()

    var inputField: TextArea by singleAssign()

    override val root = borderpane {
        addClass(Styles.mainScreen)
        top {
            hbox {
                button("Tokens") {
                    setOnAction {
                        find<AstView>().openModal()
                    }
                }
                arrowLabel()
                button("Parse tree")
                arrowLabel()
                vbox {
                    button("   AST   ") {
                        setOnAction {
                            controller.text = inputField.text
                            find<AstView>().openWindow()
                            find<AstView>().show()
                        }
                    }
                    button("Symbols") {
                        vboxConstraints { marginTop = 5.0 }
                        vboxConstraints { marginBottom = 10.0 }
                    }
                }
                arrowLabel()
                button("Bytecode")
                arrowLabel()
                button("Execute")
            }
        }
        center {
            hbox {
                inputField = textarea {

                }

            }
        }
    }

    fun EventTarget.arrowLabel() = label(" ➔ ") {
        addClass(Styles.arrowLabel)
    }
}

