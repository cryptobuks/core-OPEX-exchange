package co.nilin.mixchange.wallet.app.listener

import co.nilin.mixchange.wallet.core.model.Amount
import co.nilin.mixchange.wallet.core.model.Wallet
import co.nilin.mixchange.wallet.core.spi.WalletListener
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class WalletListenerImpl: WalletListener {
    override fun onDeposit(
        me: Wallet,
        sourceWallet: Wallet,
        amount: Amount,
        finalAmount: BigDecimal,
        transaction: String
    ) {

    }

    override fun onWithdraw(me: Wallet, destWallet: Wallet, amount: Amount, transaction: String) {

    }
}