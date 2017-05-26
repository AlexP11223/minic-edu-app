package miniceduapp.viewmodels

import miniceduapp.BaseTornadoFxComponentTest
import org.junit.Test
import tornadofx.*
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class TokensViewModelTest : BaseTornadoFxComponentTest() {

    val vm = TokensViewModel(updateDelay = 100.millis)

    @Test
    fun loadsTokens() {
        assertEquals(0, vm.tokens.size)

        vm.mainViewModel.programCode = "int x = 42;"
        vm.loadTokens()

        waitForUi { vm.status.running.not().value }

        assertNotEquals(0, vm.tokens.size)

        val prevTokensCount = vm.tokens.size

        vm.loadTokens()

        waitForUi { vm.status.running.not().value }

        assertEquals(prevTokensCount, vm.tokens.size)
    }

    @Test
    fun updatesTokens() {
        assertEquals(0, vm.tokens.size)

        vm.mainViewModel.programCode = "int x = 42;"
        vm.loadTokens()

        waitForUi { vm.status.running.not().value }

        assertNotEquals(0, vm.tokens.size)

        val prevTokensCount = vm.tokens.size

        vm.mainViewModel.programCode = "int x = 42 + 1;"
        vm.loadTokens()

        waitForUi { vm.status.running.not().value }

        assertEquals(prevTokensCount + 2, vm.tokens.size)
    }
}
