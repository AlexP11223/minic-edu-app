package miniceduapp.viewmodels

import de.saxsys.mvvmfx.testingutils.jfxrunner.*
import miniceduapp.BaseTornadoFxComponentTest
import org.junit.Test
import org.junit.runner.RunWith
import tornadofx.*
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@RunWith(JfxRunner::class)
class TokensViewModelTest : BaseTornadoFxComponentTest() {

    val vm = TokensViewModel(updateDelay = 100.millis)

    @Test
    @TestInJfxThread
    fun loadsTokens() {
        assertEquals(0, vm.tokens.size)

        vm.mainViewModel.programCode = "int x = 42;"
        vm.loadTokens()

        vm.status.completed.awaitUntil()

        assertNotEquals(0, vm.tokens.size)

        val prevTokensCount = vm.tokens.size

        vm.loadTokens()

        vm.status.completed.awaitUntil()

        assertEquals(prevTokensCount, vm.tokens.size)
    }

    @Test
    @TestInJfxThread
    fun updatesTokens() {
        assertEquals(0, vm.tokens.size)

        vm.mainViewModel.programCode = "int x = 42;"
        vm.loadTokens()

        vm.status.completed.awaitUntil()

        assertNotEquals(0, vm.tokens.size)

        val prevTokensCount = vm.tokens.size

        vm.mainViewModel.programCode = "int x = 42 + 1;"

        vm.status.running.awaitUntil()
        vm.status.completed.awaitUntil()

        assertEquals(prevTokensCount + 2, vm.tokens.size)
    }
}
