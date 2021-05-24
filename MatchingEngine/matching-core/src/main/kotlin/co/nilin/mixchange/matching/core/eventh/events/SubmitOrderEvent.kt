package co.nilin.mixchange.matching.core.eventh.events

import co.nilin.mixchange.matching.core.model.MatchConstraint
import co.nilin.mixchange.matching.core.model.OrderDirection
import co.nilin.mixchange.matching.core.model.OrderType

class SubmitOrderEvent() : CoreEvent() , OneOrderEvent{
    var ouid: String = ""
    var uuid: String = ""
    var orderId: Long? = null
    var price: Long = 0
    var quantity: Long = 0
    var remainedQuantity: Long = 0
    var direction: OrderDirection = OrderDirection.ASK
    var matchConstraint: MatchConstraint = MatchConstraint.GTC
    var orderType: OrderType = OrderType.LIMIT_ORDER

    constructor(ouid: String,
                uuid: String,
                orderId: Long?,
                pair: co.nilin.mixchange.matching.core.model.Pair,
                price: Long,
                quantity: Long,
                remainedQuantity: Long,
                direction: OrderDirection,
                matchConstraint: MatchConstraint,
                orderType: OrderType)
            : this() {
        this.ouid = ouid
        this.uuid = uuid
        this.orderId = orderId
        this.pair = pair
        this.price = price
        this.quantity = quantity
        this.remainedQuantity = remainedQuantity
        this.direction = direction
        this.matchConstraint = matchConstraint
        this.orderType = orderType
    }

    override fun ouid(): String {
        return ouid
    }

    override fun uuid(): String {
        return uuid
    }
}