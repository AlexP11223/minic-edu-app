package miniceduapp.viewmodels

import de.saxsys.mvvmfx.testingutils.jfxrunner.*
import miniceduapp.BaseTornadoFxComponentTest
import miniceduapp.views.events.ErrorMessageEvent
import miniceduapp.views.events.RequestFilePathEvent
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@RunWith(JfxRunner::class)
class MainViewModelTest : BaseTornadoFxComponentTest() {

    val vm = MainViewModel()

    @Rule
    @JvmField
    val tmpFolder = TemporaryFolder()

    @Test
    @TestInJfxThread
    fun hasValidInitialState() {
        assertEquals("", vm.programCode)
        assertEquals("", vm.filePath)
        assertEquals(false, vm.hasUnsavedCode)

        assertTrue(vm.saveCodeFileCommand.isEnabled)
        assertTrue(vm.saveNewCodeFileCommand.isEnabled)
        assertTrue(vm.openCodeFileCommand.isEnabled)
        assertTrue(vm.createNewCodeCommand.isEnabled)
    }

    @Test
    @TestInJfxThread
    fun loadsSampleCode() {
        assertTrue(vm.loadSampleCodeCommand.isEnabled)
        vm.loadSampleCodeCommand.execute()

        assertNotEquals("", vm.programCode)
        assertEquals("", vm.filePath)
        assertEquals(false, vm.hasUnsavedCode)
    }

    @Test
    @TestInJfxThread
    fun loadsFile() {
        val filepath = tmpFolder.root.absolutePath + "/program.mc"

        File(filepath).writeText("hi")

        assertTrue(vm.openCodeFileCommand.isEnabled)
        vm.openCodeFileCommand.execute(filepath)

        assertEquals("hi", vm.programCode)
        assertEquals(filepath, vm.filePath)

        assertTrue(vm.openCodeFileCommand.isEnabled)
    }

    @Test
    @TestInJfxThread
    fun firesErrorIfFileNotExists() {
        val filepath = tmpFolder.root.absolutePath + "/not_existing.mc"

        var error = ""
        subscribe<ErrorMessageEvent> {
            error = it.text
            //unsubscribe()
        }

        ignoreErrors {
            vm.openCodeFileCommand.execute(filepath)
        }

        assertThat(error, containsString("not found"))
    }

    @Test
    @TestInJfxThread
    fun savesFile() {
        vm.programCode = "hello"

        val filepath = tmpFolder.root.absolutePath + "/program.mc"
        subscribe<RequestFilePathEvent> {
            it.result = filepath
            unsubscribe()
        }

        assertTrue(vm.saveCodeFileCommand.isEnabled)
        vm.saveCodeFileCommand.execute()

        assertTrue(File(filepath).exists())
        assertEquals("hello", File(filepath).readText())

        vm.programCode = "hello world"

        assertTrue(vm.saveCodeFileCommand.isEnabled)
        vm.saveCodeFileCommand.execute()

        assertTrue(File(filepath).exists())
        assertEquals("hello world", File(filepath).readText())
    }

    @Test
    @TestInJfxThread
    fun savesNewFile() {
        vm.programCode = "hello"

        val filepath = tmpFolder.root.absolutePath + "/program.mc"
        subscribe<RequestFilePathEvent> {
            it.result = filepath
            unsubscribe()
        }

        assertTrue(vm.saveCodeFileCommand.isEnabled)
        vm.saveCodeFileCommand.execute()

        assertTrue(File(filepath).exists())
        assertEquals("hello", File(filepath).readText())

        vm.programCode = "hello world"

        val newFilepath = tmpFolder.root.absolutePath + "/new_program.mc"

        assertTrue(vm.saveNewCodeFileCommand.isEnabled)
        vm.saveNewCodeFileCommand.execute(newFilepath)

        assertTrue(File(newFilepath).exists())
        assertEquals("hello world", File(newFilepath).readText())
        assertEquals("hello", File(filepath).readText())
    }

    @Test
    @TestInJfxThread
    fun saveDisabledOnlyWhenNoChangesAfterSaveOrOpen() {
        vm.createNewCodeCommand.execute()

        assertTrue(vm.saveCodeFileCommand.isEnabled)

        val filepath = tmpFolder.root.absolutePath + "/program.mc"

        subscribe<RequestFilePathEvent> {
            it.result = filepath
            unsubscribe()
        }

        vm.saveCodeFileCommand.execute()

        assertFalse(vm.saveCodeFileCommand.isEnabled)

        vm.programCode = "hello"

        assertTrue(vm.saveCodeFileCommand.isEnabled)

        assertTrue(vm.openCodeFileCommand.isEnabled)
        vm.openCodeFileCommand.execute(filepath)

        assertFalse(vm.saveCodeFileCommand.isEnabled)

        vm.programCode = "hello"

        assertTrue(vm.saveCodeFileCommand.isEnabled)
    }

    @Test
    @TestInJfxThread
    fun canAbortFileSave() {
        vm.createNewCodeCommand.execute()

        assertTrue(vm.saveCodeFileCommand.isEnabled)

        subscribe<RequestFilePathEvent> {
            //it.result = null
            unsubscribe()
        }

        vm.saveCodeFileCommand.execute()

        assertTrue(vm.saveCodeFileCommand.isEnabled)
    }
}
