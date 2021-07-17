package co.nilin.mixchange.accountant.core.service

import co.nilin.mixchange.accountant.core.api.OrderManager
import co.nilin.mixchange.accountant.core.model.FinancialAction
import co.nilin.mixchange.accountant.core.model.PairConfig
import co.nilin.mixchange.accountant.core.model.PairFeeConfig
import co.nilin.mixchange.accountant.core.spi.*
import co.nilin.mixchange.matching.core.eventh.events.SubmitOrderEvent
import co.nilin.mixchange.matching.core.model.MatchConstraint
import co.nilin.mixchange.matching.core.model.OrderDirection
import co.nilin.mixchange.matching.core.model.OrderType
import co.nilin.mixchange.matching.core.model.Pair
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.time.LocalDateTime

internal class OrderManagerImplTest() {

    @Mock
    lateinit var financialActionPersister: FinancialActionPersister

    @Mock
    lateinit var financialActionLoader: FinancialActionLoader

    @Mock
    lateinit var orderPersister: OrderPersister

    @Mock
    lateinit var tempEventPersister: TempEventPersister

    @Mock
    lateinit var tempEventRepublisher: TempEventRepublisher

    @Mock
    lateinit var pairConfigLoader: PairConfigLoader

    @Mock
    lateinit var richOrderPublisher: RichOrderPublisher

    val orderManager: OrderManager

    init {
        MockitoAnnotations.openMocks(this)
        orderManager = OrderManagerImpl(
                pairConfigLoader, financialActionPersister, financialActionLoader, orderPersister, tempEventPersister, tempEventRepublisher, richOrderPublisher
        )
        runBlocking {
            Mockito.`when`(tempEventPersister.loadTempEvents(anyString())).thenReturn(emptyList())
        }
    }

    @Test
    fun givenAskOrder_whenHandleRequestOrder_thenFAMatch() {
        runBlocking {
            //given
            val pair = Pair("eth", "btc")
            val pairConfig = PairConfig(
                    pair.toString(), pair.leftSideName, pair.rightSideName, 1.0, 0.001
            )
            val submitOrderEvent = SubmitOrderEvent(
                    "ouid", "uuid", null, pair, 30, 60, 0, OrderDirection.ASK, MatchConstraint.GTC, OrderType.LIMIT_ORDER
            )
            Mockito.`when`(pairConfigLoader.load(pair.toString(), submitOrderEvent.direction, ""))
                    .thenReturn(PairFeeConfig(pairConfig, submitOrderEvent.direction.toString(), "", 0.1, 0.12))
            Mockito.`when`(financialActionPersister.persist(MockitoHelper.anyObject()))
                    .then {
                        return@then it.getArgument<List<FinancialAction>>(0)
                    }

            //when
            val financialActions = orderManager.handleRequestOrder(submitOrderEvent)

            //then
            assertEquals(1, financialActions.size)
            val expectedFinancialAction = FinancialAction(
                    null,
                    SubmitOrderEvent::class.simpleName!!,
                    submitOrderEvent.ouid,
                    pair.leftSideName,
                    pairConfig.leftSideFraction.toBigDecimal().multiply(submitOrderEvent.quantity.toBigDecimal()),
                    submitOrderEvent.uuid,
                    "main",
                    submitOrderEvent.uuid,
                    "exchange",
                    LocalDateTime.now()
            )
            assertEquals(expectedFinancialAction.eventType, financialActions[0].eventType)
            assertEquals(expectedFinancialAction.symbol, financialActions[0].symbol)
            assertEquals(expectedFinancialAction.amount, financialActions[0].amount)
            assertEquals(expectedFinancialAction.sender, financialActions[0].sender)
            assertEquals(expectedFinancialAction.senderWalletType, financialActions[0].senderWalletType)
            assertEquals(expectedFinancialAction.receiver, financialActions[0].receiver)
            assertEquals(expectedFinancialAction.receiverWalletType, financialActions[0].receiverWalletType)
        }
    }

    @Test
    fun givenBidOrder_whenHandleRequestOrder_thenFAMatch() {
        runBlocking {
            //given
            val pair = Pair("eth", "btc")
            val pairConfig = PairConfig(
                    pair.toString(), pair.leftSideName, pair.rightSideName, 1.0, 0.001
            )
            val submitOrderEvent = SubmitOrderEvent(
                    "ouid", "uuid", null, pair, 35, 14, 0, OrderDirection.BID, MatchConstraint.GTC, OrderType.LIMIT_ORDER
            )
            Mockito.`when`(pairConfigLoader.load(pair.toString(), submitOrderEvent.direction, ""))
                    .thenReturn(
                            PairFeeConfig(
                                    pairConfig, submitOrderEvent.direction.toString(), "", 0.08, 0.1
                            )
                    )
            Mockito.`when`(financialActionPersister.persist(MockitoHelper.anyObject()))
                    .then {
                        return@then it.getArgument<List<FinancialAction>>(0)
                    }

            //when
            val financialActions = runBlocking {
                orderManager.handleRequestOrder(submitOrderEvent)
            }

            //then
            assertEquals(1, financialActions.size)
            val expectedFinancialAction = FinancialAction(
                    null,
                    SubmitOrderEvent::class.simpleName!!,
                    submitOrderEvent.ouid,
                    pair.rightSideName,
                    pairConfig.leftSideFraction.toBigDecimal().multiply(submitOrderEvent.quantity.toBigDecimal())
                            .multiply(pairConfig.rightSideFraction.toBigDecimal())
                            .multiply(submitOrderEvent.price.toBigDecimal()),
                    submitOrderEvent.uuid,
                    "main",
                    submitOrderEvent.uuid,
                    "exchange",
                    LocalDateTime.now()
            )
            assertEquals(expectedFinancialAction.eventType, financialActions[0].eventType)
            assertEquals(expectedFinancialAction.symbol, financialActions[0].symbol)
            assertEquals(expectedFinancialAction.amount, financialActions[0].amount)
            assertEquals(expectedFinancialAction.sender, financialActions[0].sender)
            assertEquals(expectedFinancialAction.senderWalletType, financialActions[0].senderWalletType)
            assertEquals(expectedFinancialAction.receiver, financialActions[0].receiver)
            assertEquals(expectedFinancialAction.receiverWalletType, financialActions[0].receiverWalletType)
        }
    }

    @Test
    fun handleNewOrder() {
    }

    @Test
    fun handleUpdateOrder() {
    }

    @Test
    fun handleRejectOrder() {
    }

    @Test
    fun handleCancelOrder() {
    }
}


