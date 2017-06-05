package miniceduapp.viewmodels

import de.saxsys.mvvmfx.testingutils.jfxrunner.*
import miniceduapp.testutils.BaseTornadoFxComponentTest
import org.junit.Test
import org.junit.runner.RunWith
import tornadofx.*
import kotlin.test.*

@RunWith(JfxRunner::class)
class AstViewModelTest : BaseTornadoFxComponentTest() {

    val vm = AstViewModel(updateDelay = 100.millis)

    @Test
    @TestInJfxThread
    fun loadsAst() {
        assertNull(vm.astImage)

        vm.mainViewModel.programCode = "int x = 86;"
        vm.loadAst()

        vm.status.completed.awaitUntil()

        assertNotNull(vm.astImage)
        assertEquals("int x = 86;", vm.programCode)
    }
}
