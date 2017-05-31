package miniceduapp.views

import javafx.application.Platform
import javafx.event.EventTarget
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.input.KeyCode
import javafx.scene.layout.Priority
import javafx.stage.FileChooser
import miniceduapp.helpers.messageOrString
import miniceduapp.viewmodels.MainViewModel
import miniceduapp.views.editor.*
import miniceduapp.views.events.*
import miniceduapp.views.styles.Styles
import org.fxmisc.richtext.CodeArea
import tornadofx.*
import java.io.File
import java.nio.file.Paths

class MainView : View("Mini-C vizualization/simulation") {
    val viewModel: MainViewModel by inject()

    var codeArea: CodeArea by singleAssign()
    var outputArea: CodeArea by singleAssign()

    var initialDialogDir = "demo"

    private var scrollingOutput = false // for autoscrolling to bottom via timer, doesn't work otherwise, not sure why

    override val root = borderpane {
        top {
            menubar {
                menu("File") {
                    item("New", "Shortcut+N").command = viewModel.createNewCodeCommand
                    item("Open", "Shortcut+O") {
                        setOnAction {
                            openCodeFile()
                        }
                        enableWhen { viewModel.openCodeFileCommand.enabled }
                    }
                    item("Save", "Shortcut+S").command = viewModel.saveCodeFileCommand
                    item("Save as", "Shortcut+Shift+S") {
                        setOnAction {
                            saveNewCodeFile()
                        }
                        enableWhen { viewModel.saveNewCodeFileCommand.enabled }
                    }
                    item("Quit", "Shortcut+Q").action {
                        Platform.exit()
                    }
                }
            }
        }
        center {
            vbox(10) {
                addClass(Styles.windowContent)
                hbox {
                    button("", imageview("new.png")) {
                        addClass(Styles.iconButton)
                        tooltip("New (Ctrl+N)")
                        command = viewModel.createNewCodeCommand
                    }
                    button("", imageview("open.png")) {
                        addClass(Styles.iconButton)
                        tooltip("Open (Ctrl+O)")
                        setOnAction {
                            openCodeFile()
                        }
                        enableWhen { viewModel.openCodeFileCommand.enabled }
                    }
                    button("", imageview("save.png")) {
                        addClass(Styles.iconButton)
                        tooltip("Save (Ctrl+S)")
                        command = viewModel.saveCodeFileCommand
                    }
                }
                vbox {
                    vgrow = Priority.ALWAYS
                    hbox {
                        label(viewModel.filePathProperty.stringBinding { Paths.get(it).fileName.toString() }) {
                            toggleClass(Styles.modifiedInput, viewModel.hasUnsavedCodeProperty)
                        }
                        label("*") {
                            addClass(Styles.modifiedInput)
                            visibleWhen { viewModel.hasUnsavedCodeProperty.and(viewModel.filePathProperty.isNotEmpty) }
                        }
                    }
                    codeArea = codeEditor(paneOp = {
                        hgrow = Priority.ALWAYS
                        vgrow = Priority.ALWAYS
                    }) {
                        addSyntaxHighlighting(MiniCSyntaxHighlighter())
                        showLineNumbers()
                        editableProperty().bind(viewModel.isExecutingProgramProperty.not())
                    }
                }
                hbox {
                    button("Tokens") {
                        setOnAction {
                            find<TokensView>().openWindow()
                        }
                    }
                    arrowLabel()
                    vbox(5) {
                        button("   AST   ") {
                            setOnAction {
                                find<AstView>().openWindow()
                            }
                        }
                        button("Symbols") {
                        }
                    }
                    arrowLabel()
                    button("Bytecode")
                    arrowLabel()
                    vbox(5) {
                        button("Execute", imageview("run.png")) {
                            shortcut("F9")
                            tooltip("Run the program (F9)")
                            command = viewModel.executeCodeCommand
                        }
                        button("Stop", imageview("stop.png")) {
                            shortcut("F10")
                            tooltip("Stop the program (F10)")
                            command = viewModel.stopCodeExecutionCommand
                            visibleWhen { viewModel.isExecutingProgramProperty }
                        }
                    }
                }
                vbox(10) {
                    vbox {
                        label("Output")
                        outputArea = codeEditor(paneOp = {
                            minHeight = 140.0
                            maxHeight = 140.0
                        }) {
                            addClass(Styles.outputArea)
                            isEditable = false
                            addSyntaxHighlighting(ProgramExecutionHighlighter())
                        }
                    }
                    hbox(10) {
                        label("Input") {
                            style {
                                alignment = Pos.CENTER_LEFT
                            }
                        }
                        textfield(viewModel.inputProperty) {
                            hgrow = Priority.ALWAYS
                            setOnKeyPressed {
                                if (it.code == KeyCode.ENTER) {
                                    viewModel.writeInputCommand.execute()
                                }
                            }
                        }
                        button("Enter") {
                            command = viewModel.writeInputCommand
                        }
                        removeWhen { viewModel.isExecutingProgramProperty.not().or(viewModel.hasInputOperationsProperty.not()) }
                    }
                }
            }
        }
    }

    init {
        codeArea.textProperty().onChange {
            viewModel.programCode = it!!
        }
        viewModel.programCodeProperty.onChange {
            if (it != codeArea.text) {
                codeArea.replaceText(it)
            }
        }
        viewModel.outputProperty.onChange {
            outputArea.replaceText(it)

            // autoscroll
            if (!scrollingOutput) {
                scrollingOutput = true
                runLater(10.millis) {
                    outputArea.scrollBy(Point2D(0.0, Double.MAX_VALUE))
                    scrollingOutput = false
                }
            }
        }

        viewModel.isExecutingProgramProperty.onChange {
            runLater(100.millis) {
                outputArea.scrollBy(Point2D(0.0, Double.MAX_VALUE))
            }
        }

        subscribe<ErrorEvent> {
            alert(Alert.AlertType.ERROR, "Error", it.text ?: it.error.messageOrString(), ButtonType.OK)
        }
        subscribe<ErrorMessageEvent> {
            alert(Alert.AlertType.ERROR, "Error", it.text, ButtonType.OK)
        }
        subscribe<RequestFilePathEvent> {
            it.result = browseFile(FileChooserMode.Save, it.filters)
        }

        viewModel.loadSampleCodeCommand.execute()
    }

    override fun onDock() {
        setWindowMinSize(500, 400)

        primaryStage.width = 600.0
        primaryStage.height = 700.0
    }

    private fun EventTarget.arrowLabel() = label(" ➔ ") {
        addClass(Styles.arrowLabel)
    }

    private fun browseFile(mode: FileChooserMode, filters: List<FileExtensionFilter> = viewModel.codeFileFilters): String? {
        val fxFilters = filters.map { FileChooser.ExtensionFilter(it.description, it.extensions) }.toTypedArray()
        val result = chooseFile("", fxFilters, mode, currentWindow) {
            if (File(initialDialogDir).exists()) {
                initialDirectory = File(initialDialogDir)
            }
        }
        if (result.any()) {
            initialDialogDir = result.first().parent
        }
        return result.firstOrNull()?.absolutePath
    }

    private fun saveNewCodeFile() {
        val filePath = browseFile(FileChooserMode.Save)
        if (filePath != null) {
            viewModel.saveNewCodeFileCommand.execute(filePath)
        }
    }

    private fun openCodeFile() {
        val filePath = browseFile(FileChooserMode.Single)
        if (filePath != null) {
            viewModel.openCodeFileCommand.execute(filePath)
        }
    }
}

