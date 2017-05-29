package miniceduapp.viewmodels

import javafx.beans.property.*
import miniceduapp.helpers.messageOrString
import miniceduapp.views.events.ErrorEvent
import miniceduapp.views.events.ErrorMessageEvent
import miniceduapp.views.events.FileExtensionFilter
import miniceduapp.views.events.RequestFilePathEvent
import tornadofx.*
import java.io.File

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

    val codeFileFilters = listOf(FileExtensionFilter("Mini-C source code", listOf("*.mc")))

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
}
