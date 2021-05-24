package co.nilin.mixchange.port.accountant.postgres.model


import co.nilin.mixchange.accountant.core.model.FinancialAction
import co.nilin.mixchange.accountant.core.model.FinancialActionStatus
import co.nilin.mixchange.matching.core.model.OrderDirection
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Table("fi_actions")
class FinancialActionModel(
    @Id var id: Long?,
    @Column("parent_id") var parentId: Long?,
    @Column("event_type") val eventType: String,
    val pointer: String,
    val symbol: String,
    @Column("amount") val amount: Double,
    val sender: String,
    @Column("sender_wallet_type") val senderWalletType: String,
    val receiver: String,
    @Column("receiver_wallet_type") val receiverWalletType: String,
    val agent: String,
    val ip: String,
    @Column("create_date") val createDate: LocalDateTime,
    val status: FinancialActionStatus = FinancialActionStatus.CREATED,
    @Column("retry_count") val retryCount: Int? = null,
    @Column("last_try_date") val lastTryDate: LocalDateTime? = null
)


