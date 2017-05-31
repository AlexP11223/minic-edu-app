package miniceduapp.viewmodels

import javafx.beans.property.*
import miniceduapp.CodeExecutor
import miniceduapp.helpers.messageOrString
import miniceduapp.views.events.ErrorEvent
import miniceduapp.views.events.ErrorMessageEvent
import miniceduapp.views.events.FileExtensionFilter
import miniceduapp.views.events.RequestFilePathEvent
import org.apache.commons.io.FilenameUtils
import tornadofx.*
import java.io.File
import java.nio.file.Paths

class MainViewModel : ViewModel() {
    val programCodeProperty = SimpleStringProperty("")
    var programCode: String by programCodeProperty

    private val _filePathProperty = ReadOnlyStringWrapper("")
    private var _filePath by _filePathProperty
    val filePathProperty: ReadOnlyStringProperty get() = _filePathProperty.readOnlyProperty
    val filePath: String get() = _filePathProperty.value

    private val _hasUnsavedCodeProperty = ReadOnlyBooleanWrapper(false)
    private var _hasUnsavedCode by _hasUnsavedCodeProperty
    val hasUnsavedCodeProperty: ReadOnlyBooleanProperty get() = _hasUnsavedCodeProperty.readOnlyProperty
    val hasUnsavedCode: Boolean get() = _hasUnsavedCodeProperty.value

    private val _isExecutingProgramProperty = ReadOnlyBooleanWrapper(false)
    private var _isExecutingProgram by _isExecutingProgramProperty
    val isExecutingProgramProperty: ReadOnlyBooleanProperty get() = _isExecutingProgramProperty.readOnlyProperty
    val isExecutingProgram: Boolean get() = _isExecutingProgramProperty.value

    private val _hasInputOperationsProperty = ReadOnlyBooleanWrapper(false)
    private var _hasInputOperations by _hasInputOperationsProperty
    val hasInputOperationsProperty: ReadOnlyBooleanProperty get() = _hasInputOperationsProperty.readOnlyProperty
    val hasInputOperations: Boolean get() = _hasInputOperationsProperty.value

    private val _outputProperty = ReadOnlyStringWrapper("")
    private var _output by _outputProperty
    val outputProperty: ReadOnlyStringProperty get() = _outputProperty.readOnlyProperty
    val output: String get() = _outputProperty.value

    val inputProperty = SimpleStringProperty("")
    var input: String by inputProperty

    private var codeExecutor: CodeExecutor? = null

    val codeFileFilters = listOf(
            FileExtensionFilter("Mini-C source code", listOf("*.mc")),
            FileExtensionFilter("All files", listOf("*.*"))
    )

    init {
        programCodeProperty.onChange {
            _hasUnsavedCode = true
        }
    }

    val saveCodeFileCommand = command(this::saveCodeFile,
            enabled = hasUnsavedCodeProperty.or(filePathProperty.isEmpty))

    val saveNewCodeFileCommand = command<String>(this::saveCodeFile)

    val openCodeFileCommand = command(this::openCodeFile)

    val createNewCodeCommand = command(this::createNewCode)

    val loadSampleCodeCommand = command(this::loadSampleCode)

    val executeCodeCommand = command(this::executeCode,
            enabled = isExecutingProgramProperty.not())

    val stopCodeExecutionCommand = command(this::stopCodeExecution,
            enabled = isExecutingProgramProperty)

    val writeInputCommand = command(this::writeInput,
            enabled = isExecutingProgramProperty)

    private fun saveCodeFile() {
        val fp = if (filePath.isNullOrEmpty()) {
            val request = RequestFilePathEvent(codeFileFilters)
            fire(request)
            if (request.result == null) {
                return
            }
            request.result!!
        } else {
            this.filePath
        }

        saveCodeFile(fp)
    }

    private fun saveCodeFile(newFilePath: String) {
        try {
            File(newFilePath).writeText(programCode)
            _filePath = newFilePath
            _hasUnsavedCode = false
        } catch (ex: Throwable) {
            fire(ErrorEvent(ex, "Failed to save file '$newFilePath': ${ex.messageOrString()}."))
        }
    }

    private fun openCodeFile(newFilePath: String) {
        if (!File(newFilePath).exists()) {
            fire(ErrorMessageEvent("File '$newFilePath' not found."))
            return
        }

        try {
            programCode = File(newFilePath).readText()
            _filePath = newFilePath
            _hasUnsavedCode = false
        } catch (ex: Throwable) {
            fire(ErrorEvent(ex, "Failed to open file '$newFilePath': ${ex.messageOrString()}."))
        }
    }

    private fun createNewCode() {
        programCode = ""
        _filePath = ""
        _hasUnsavedCode = false
    }

    private fun loadSampleCode() {
        createNewCode()
        programCode = """println("Hello");
int x = 42;
int y = x + 8 * 2 / (3 - 1);
print("x: " + toString(y));
"""
        _hasUnsavedCode = false
    }
    
    private fun executeCode() {
        _output = ""
        _isExecutingProgram = true

        val simulatedFileName = if (filePath.isNullOrEmpty()) "program.mc" else Paths.get(filePath).fileName.toString()
        _output += "> minic $simulatedFileName\n"

        codeExecutor = CodeExecutor(programCode, onOutput = {
            _output += it
        }, onFail = {
            fire(ErrorEvent(it))
        }, onFinish = {
            _isExecutingProgram = false
        }, onCompiled = {
            _output += "> java ${FilenameUtils.getBaseName(simulatedFileName)}\n"
        })
        _hasInputOperations = codeExecutor!!.hasInputOperations

        codeExecutor!!.start()
    }

    private fun stopCodeExecution() {
        codeExecutor?.stop()
    }

    private fun writeInput() {
        val str = input.trim() + "\n"
        _output += str
        input = ""
        codeExecutor?.writeInput(str)
    }
}
