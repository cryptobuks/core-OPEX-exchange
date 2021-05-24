package co.nilin.mixchange.wallet.app.controller

import co.nilin.mixchange.wallet.core.model.Amount
import co.nilin.mixchange.wallet.core.model.Currency
import co.nilin.mixchange.wallet.core.spi.WalletManager
import co.nilin.mixchange.wallet.core.spi.WalletOwnerManager
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.lang.RuntimeException
import java.math.BigDecimal

@RestController
class WalletController(
    val walletManager: WalletManager, val walletOwnerManager: WalletOwnerManager
) {
    val logger =  LoggerFactory.getLogger(WalletController::class.java)

    data class BooleanResponse(val result: Boolean)
    @GetMapping("{uuid}/wallet_type/{wallet_type}/can_withdraw/{amount}_{currency}")
    suspend fun canFulfill(
        @PathVariable("uuid") uuid: String,
        @PathVariable("currency") currency: String,
        @PathVariable("wallet_type") walletType: String,
        @PathVariable("amount") amount: BigDecimal
    ): BooleanResponse {
        logger.info("canFullFill: {} {} {} {}", uuid, currency, walletType, amount)
        val owner = walletOwnerManager.findWalletOwner(uuid)
        if (owner != null) {
            val wallet = walletManager.findWalletByOwnerAndCurrencyAndType(owner, walletType, Symbol(currency))
            if (wallet != null) {
                return BooleanResponse(
                    walletManager.isWithdrawAllowed(wallet, amount)
                            && walletOwnerManager.isWithdrawAllowed(owner, Amount(wallet.currency(), amount)) )
            }
        }
        return BooleanResponse(false)
    }
}