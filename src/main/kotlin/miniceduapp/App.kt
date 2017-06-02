package miniceduapp

import javafx.application.Application
import miniceduapp.views.MainView
import miniceduapp.views.styles.*
import tornadofx.*


class MyApp: App(MainView::class, Styles::class, CodeHighlightStyles::class, ProgramExecutionStyles::class, SelectionHighlightStyles::class)

fun main(args: Array<String>) {
    importStylesheet("/richtextfx.css")

    Application.launch(MyApp::class.java, *args)
}
