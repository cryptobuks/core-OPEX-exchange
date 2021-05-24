package co.nilin.mixchange.port.wallet.postgres.dao

import co.nilin.mixchange.port.wallet.postgres.model.WalletModel
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.math.BigDecimal

@Repository
interface WalletRepository : ReactiveCrudRepository<WalletModel, Long> {
    @Query("select * from wallet where owner = :owner and wallet_type = :type and currency = :currency ")
    fun findByOwnerAndTypeAndCurrency(@Param("owner") owner: Long,
                                      @Param("type") type: String,
                                      @Param("currency") currency: String): Mono<WalletModel?>

    @Modifying
    @Query("update wallet set balance = balance + :balance where id = :id")
    fun updateBalance(id: Long, delta: BigDecimal): Mono<Int>
}