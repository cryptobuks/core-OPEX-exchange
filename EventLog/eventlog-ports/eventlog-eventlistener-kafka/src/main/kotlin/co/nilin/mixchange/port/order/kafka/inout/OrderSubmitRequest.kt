package co.nilin.mixchange.port.order.kafka.inout

import co.nilin.mixchange.matching.core.model.MatchConstraint
import co.nilin.mixchange.matching.core.model.OrderDirection
import co.nilin.mixchange.matching.core.model.OrderType

public class OrderSubmitRequest() {
    lateinit var ouid: String
    lateinit var uuid: String
    var orderId: Long? = null
    lateinit var pair: co.nilin.mixchange.matching.core.model.Pair
    var price: Long = 0
    var quantity: Long = 0
    var direction: OrderDirection = OrderDirection.BID
    var matchConstraint: MatchConstraint = MatchConstraint.GTC
    var orderType: OrderType = OrderType.LIMIT_ORDER

    constructor(ouid: String,
                uuid: String,
                orderId: Long?,
                pair: co.nilin.mixchange.matching.core.model.Pair,
                price: Long,
                quantity: Long,
                direction: OrderDirection,
                matchConstraint: MatchConstraint,
                orderType: OrderType):this(){
        this.ouid = ouid
        this.uuid = uuid
        this.orderId = orderId
        this.pair = pair
        this.price = price
        this.quantity = quantity
        this.direction = direction
        this.matchConstraint = matchConstraint
        this.orderType = orderType
    }


}