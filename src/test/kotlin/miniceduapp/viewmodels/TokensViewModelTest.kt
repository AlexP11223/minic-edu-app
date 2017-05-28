package miniceduapp.viewmodels

import de.saxsys.mvvmfx.testingutils.jfxrunner.*
import miniceduapp.BaseTornadoFxComponentTest
import miniceduapp.awaitUntil
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

        vm.status.running.not().awaitUntil()

        assertNotEquals(0, vm.tokens.size)

        val prevTokensCount = vm.tokens.size

        vm.loadTokens()

        vm.status.running.not().awaitUntil()

        assertEquals(prevTokensCount, vm.tokens.size)
    }

    @Test
    @TestInJfxThread
    fun updatesTokens() {
        assertEquals(0, vm.tokens.size)

        vm.mainViewModel.programCode = "int x = 42;"
        vm.loadTokens()

        vm.status.running.not().awaitUntil()

        assertNotEquals(0, vm.tokens.size)

        val prevTokensCount = vm.tokens.size

        vm.mainViewModel.programCode = "int x = 42 + 1;"
        vm.loadTokens()

        vm.status.running.not().awaitUntil()

        assertEquals(prevTokensCount + 2, vm.tokens.size)
    }
}
