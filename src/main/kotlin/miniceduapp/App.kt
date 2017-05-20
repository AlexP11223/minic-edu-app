package miniceduapp

import javafx.application.Application
import miniceduapp.views.MainView
import miniceduapp.views.styles.*
import tornadofx.App

class MyApp: App(MainView::class, Styles::class, CodeHighlightStyles::class)

/**
 * The main method is needed to support the mvn jfx:run goal.
 */
fun main(args: Array<String>) {
    Application.launch(MyApp::class.java, *args)
}
