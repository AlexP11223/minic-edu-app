package miniceduapp.views

import javafx.application.Platform
import javafx.event.EventTarget
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.TextArea
import javafx.scene.layout.Priority
import javafx.stage.FileChooser
import miniceduapp.helpers.messageOrString
import miniceduapp.viewmodels.MainViewModel
import miniceduapp.views.editor.MiniCSyntaxHighlighter
import miniceduapp.views.editor.addSyntaxHighlighting
import miniceduapp.views.editor.codeEditor
import miniceduapp.views.editor.showLineNumbers
import miniceduapp.views.events.*
import miniceduapp.views.styles.Styles
import org.fxmisc.richtext.CodeArea
import tornadofx.*
import java.nio.file.Paths

class MainView : View("Mini-C vizualization/simulation") {
    val viewModel: MainViewModel by inject()

    var codeArea: CodeArea by singleAssign()
    var outputField: TextArea by singleAssign()

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
                label(viewModel.filePathProperty.stringBinding { Paths.get(it).fileName.toString() }) {
                    toggleClass(Styles.modifiedInput, viewModel.hasUnsavedCodeProperty)
                }
                codeArea = codeEditor(paneOp = {
                    hgrow = Priority.ALWAYS
                    vgrow = Priority.ALWAYS
                }) {
                    addSyntaxHighlighting(MiniCSyntaxHighlighter())
                    showLineNumbers()
                }
                hbox {
                    button("Tokens") {
                        setOnAction {
                            find<TokensView>().openWindow()
                        }
                    }
                    arrowLabel()
                    vbox {
                        button("   AST   ") {
                            setOnAction {
                                find<AstView>().openWindow()
                            }
                        }
                        button("Symbols") {
                            vboxConstraints { marginTop = 5.0 }
                        }
                    }
                    arrowLabel()
                    button("Bytecode")
                    arrowLabel()
                    button("Execute", imageview("run.png")) {
                        shortcut("F9")
                        tooltip("Run the program (F9)")
                        setOnAction {
                            println("exec")
                        }
                    }
                }
                vbox {
                    label("Output")
                    outputField = textarea {
                        maxHeight = 100.0
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

        subscribe<ErrorEvent> {
            alert(Alert.AlertType.ERROR, "Error", it.text ?: it.error.messageOrString(), ButtonType.OK)
        }
        subscribe<ErrorMessageEvent> {
            alert(Alert.AlertType.ERROR, "Error", it.text, ButtonType.OK)
        }
        subscribe<RequestFilePathEvent> {
            val result = chooseFile("", it.filters.map { FileChooser.ExtensionFilter(it.description, it.extensions) }.toTypedArray(),
                    FileChooserMode.Save, currentWindow)
            if (!result.isEmpty()) {
                it.result = result.first().absolutePath
            }
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

    private fun browseCodeFile(mode: FileChooserMode): String? {
        val result = chooseFile("", viewModel.codeFileFilters.map { FileChooser.ExtensionFilter(it.description, it.extensions) }.toTypedArray(),
                mode, currentWindow)
        return result.firstOrNull()?.absolutePath
    }

    private fun saveNewCodeFile() {
        val filePath = browseCodeFile(FileChooserMode.Save)
        if (filePath != null) {
            viewModel.saveNewCodeFileCommand.execute(filePath)
        }
    }

    private fun openCodeFile() {
        val filePath = browseCodeFile(FileChooserMode.Single)
        if (filePath != null) {
            viewModel.openCodeFileCommand.execute(filePath)
        }
    }
}

