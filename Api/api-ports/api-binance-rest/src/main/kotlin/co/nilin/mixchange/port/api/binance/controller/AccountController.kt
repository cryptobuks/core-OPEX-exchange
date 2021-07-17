package co.nilin.mixchange.port.api.binance.controller

import co.nilin.mixchange.api.core.inout.*
import co.nilin.mixchange.api.core.spi.UserQueryHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.security.Principal
import java.util.*

@RestController
class AccountController(val queryHandler: UserQueryHandler) {

    data class FillsData(
            val price: BigDecimal,
            val qty: BigDecimal,
            val commission: BigDecimal,
            val commissionAsset: String
    )

    data class NewOrderResponse(
            val symbol: String,
            val orderId: Long,
            val orderListId: Long, //Unless OCO, value will be -1
            val clientOrderId: String,
            val transactTime: Date,
            val price: BigDecimal?,
            val origQty: BigDecimal?,
            val executedQty: BigDecimal?,
            val cummulativeQuoteQty: BigDecimal,
            val status: OrderStatus?,
            val timeInForce: TimeInForce?,
            val type: OrderType?,
            val side: OrderSide?,
            val fills: List<FillsData>?
    )

    data class QueryOrderResponse(
            val symbol: String,
            val orderId: Long,
            val orderListId: Long, //Unless part of an OCO, the value will always be -1.
            val clientOrderId: String,
            val price: BigDecimal,
            val origQty: BigDecimal,
            val executedQty: BigDecimal,
            val cummulativeQuoteQty: BigDecimal,
            val status: OrderStatus,
            val timeInForce: TimeInForce,
            val type: OrderType,
            val side: OrderSide,
            val stopPrice: BigDecimal?,
            val icebergQty: BigDecimal?,
            val time: Date,
            val updateTime: Date,
            val isWorking: Boolean,
            val origQuoteOrderQty: BigDecimal
    )

    data class TradeResponse(
            val symbol: String,
            val id: Long,
            val orderId: Long,
            val orderListId: Long = -1,
            val price: BigDecimal,
            val qty: BigDecimal,
            val quoteQty: BigDecimal,
            val commission: BigDecimal,
            val commissionAsset: String,
            val time: Date,
            val isBuyer: Boolean,
            val isMaker: Boolean,
            val isBestMatch: Boolean)

    /*
   Send in a new order.
   Weight: 1
   Data Source: Matching Engine
    */
    @PostMapping(
            "/api/v3/order",
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE),
            produces = arrayOf(MediaType.APPLICATION_JSON_VALUE)
    )
    fun createNewOrder(
            @RequestParam(name = "symbol")
            symbol: String,
            @RequestParam(name = "side")
            side: OrderSide,
            @RequestParam(name = "type")
            type: OrderType,
            @RequestParam(name = "timeInForce", required = false)
            timeInForce: TimeInForce?,
            @RequestParam(name = "quantity", required = false)
            quantity: BigDecimal?,
            @RequestParam(name = "quoteOrderQty", required = false)
            quoteOrderQty: BigDecimal?,
            @RequestParam(name = "price", required = false)
            price: BigDecimal?,
            @RequestParam(name = "newClientOrderId", required = false)
            newClientOrderId: String?,    /* A unique id among open orders. Automatically generated if not sent.
    Orders with the same newClientOrderID can be accepted only when the previous one is filled, otherwise the order will be rejected.
    */
            @RequestParam(name = "stopPrice", required = false)
            stopPrice: BigDecimal?, //Used with STOP_LOSS, STOP_LOSS_LIMIT, TAKE_PROFIT, and TAKE_PROFIT_LIMIT orders.
            @RequestParam(name = "icebergQty", required = false)
            icebergQty: BigDecimal?, //Used with LIMIT, STOP_LOSS_LIMIT, and TAKE_PROFIT_LIMIT to create an iceberg order.
            @RequestParam(name = "newOrderRespType", required = false)
            newOrderRespType: OrderResponseType?,  //Set the response JSON. ACK, RESULT, or FULL; MARKET and LIMIT order types default to FULL, all other orders default to ACK.
            @RequestParam(name = "recvWindow", required = false)
            recvWindow: Long?, //The value cannot be greater than 60000
            @RequestParam(name = "timestamp")
            timestamp: Long
    ): Mono<NewOrderResponse> {
        TODO("Implement create order")
    }

    /*
  Check an order's status.

  Weight: 2
  Data Source: Database
  */
    @GetMapping(
            "/api/v3/order",
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE),
            produces = arrayOf(MediaType.APPLICATION_JSON_VALUE)
    )
    suspend fun queryOrder(principal: Principal,
                           @RequestParam(name = "symbol")
                           symbol: String,
                           @RequestParam(name = "orderId", required = false)
                           orderId: Long?,
                           @RequestParam(name = "origClientOrderId", required = false)
                           origClientOrderId: String?,
                           @RequestParam(name = "recvWindow", required = false)
                           recvWindow: Long?, //The value cannot be greater than 60000
                           @RequestParam(name = "timestamp")
                           timestamp: Long
    ): QueryOrderResponse {
        val response = queryHandler.queryOrder(principal, QueryOrderRequest(symbol, orderId, origClientOrderId))
        if (response == null)
            throw IllegalArgumentException("no order found")
        return QueryOrderResponse(response.symbol, response.orderId, response.orderListId, response.clientOrderId, response.price, response.origQty, response.executedQty, response.cummulativeQuoteQty, response.status, response.timeInForce, response.type, response.side, response.stopPrice, response.icebergQty, response.time, response.updateTime, response.isWorking, response.origQuoteOrderQty)
    }


    /*
      Get all open orders on a symbol. Careful when accessing this with no symbol.

      Weight: 3 for a single symbol; 40 when the symbol parameter is omitted

      Data Source: Database
    */
    @GetMapping(
            "/api/v3/openOrders",
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE),
            produces = arrayOf(MediaType.APPLICATION_JSON_VALUE)
    )
    suspend fun fetchOpenOrders(principal: Principal,
                                @RequestParam(name = "symbol", required = false)
                                symbol: String?,
                                @RequestParam(name = "recvWindow", required = false)
                                recvWindow: Long?, //The value cannot be greater than 60000
                                @RequestParam(name = "timestamp")
                                timestamp: Long
    ): Flow<QueryOrderResponse> {
        return queryHandler.openOrders(principal, symbol)
                .map { response ->
                    QueryOrderResponse(response.symbol, response.orderId, response.orderListId, response.clientOrderId, response.price, response.origQty, response.executedQty, response.cummulativeQuoteQty, response.status, response.timeInForce, response.type, response.side, response.stopPrice, response.icebergQty, response.time, response.updateTime, response.isWorking, response.origQuoteOrderQty)
                }
    }

    /*
   Get all account orders; active, canceled, or filled.
   Weight: 10 with symbol
   Data Source: Database
   */
    @GetMapping(
            "/api/v3/allOrders",
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE),
            produces = arrayOf(MediaType.APPLICATION_JSON_VALUE)
    )
    suspend fun fetchAllOrders(principal: Principal,
                               @RequestParam(name = "symbol", required = false)
                               symbol: String?,
                               @RequestParam(name = "startTime", required = false)
                               startTime: Date?,
                               @RequestParam(name = "endTime", required = false)
                               endTime: Date?,
                               @RequestParam(name = "limit", required = false)
                               limit: Int? = 500, //Default 500; max 1000.
                               @RequestParam(name = "recvWindow", required = false)
                               recvWindow: Long?, //The value cannot be greater than 60000
                               @RequestParam(name = "timestamp")
                               timestamp: Long
    ): Flow<QueryOrderResponse> {
        return queryHandler.allOrders(principal, AllOrderRequest(symbol, startTime, endTime, limit))
                .map { response ->
                    QueryOrderResponse(response.symbol, response.orderId, response.orderListId, response.clientOrderId, response.price, response.origQty, response.executedQty, response.cummulativeQuoteQty, response.status, response.timeInForce, response.type, response.side, response.stopPrice, response.icebergQty, response.time, response.updateTime, response.isWorking, response.origQuoteOrderQty)
                }
    }

    /*
    Get trades for a specific account and symbol.
    If fromId is set, it will get trades >= that fromId. Otherwise most recent trades are returned.
    Weight: 10 with symbol
    Data Source: Database
    */
    @GetMapping(
            "/api/v3/myTrades",
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE),
            produces = arrayOf(MediaType.APPLICATION_JSON_VALUE)
    )
    suspend fun fetchAllTrades(principal: Principal,
                               @RequestParam(name = "symbol")
                               symbol: String?,
                               @RequestParam(name = "startTime", required = false)
                               startTime: Date?,
                               @RequestParam(name = "endTime", required = false)
                               endTime: Date?,
                               @RequestParam(name = "fromId", required = false)
                               fromId: Long?,//TradeId to fetch from. Default gets most recent trades.
                               @RequestParam(name = "limit", required = false)
                               limit: Int? = 500, //Default 500; max 1000.
                               @RequestParam(name = "recvWindow", required = false)
                               recvWindow: Long?, //The value cannot be greater than 60000
                               @RequestParam(name = "timestamp")
                               timestamp: Long
    ): Flow<TradeResponse> {
        return queryHandler.allTrades(principal, TradeRequest(symbol, fromId, startTime, endTime, limit))
                .map { response ->
                    TradeResponse(response.symbol, response.id,
                            response.orderId, -1, response.price, response.qty, response.quoteQty,
                            response.commission, response.commissionAsset, response.time, response.isBuyer,
                            response.isMaker, response.isBestMatch)
                }
    }

}