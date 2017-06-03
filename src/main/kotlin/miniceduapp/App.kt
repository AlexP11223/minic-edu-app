package miniceduapp

import javafx.application.Application
import miniceduapp.views.MainView
import miniceduapp.views.styles.*
import tornadofx.*


class MinicEduApp: App(MainView::class, Styles::class, CodeHighlightStyles::class, ProgramExecutionStyles::class, SelectionHighlightStyles::class)

fun main(args: Array<String>) {
    importStylesheet("/richtextfx.css")

    Application.launch(MinicEduApp::class.java, *args)
}
