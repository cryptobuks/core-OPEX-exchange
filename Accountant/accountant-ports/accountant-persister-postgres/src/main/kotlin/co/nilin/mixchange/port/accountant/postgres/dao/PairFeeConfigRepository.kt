package co.nilin.mixchange.port.accountant.postgres.dao

import co.nilin.mixchange.matching.core.model.OrderDirection
import co.nilin.mixchange.port.accountant.postgres.model.PairConfigModel
import co.nilin.mixchange.port.accountant.postgres.model.PairFeeConfigModel
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface PairFeeConfigRepository: ReactiveCrudRepository<PairFeeConfigModel, Long> {
    @Query("select * from pair_fee_config where pair_config_id = :pair and direction = :direction and user_level = :userLevel")
    fun findByPairAndDirectionAndUserLevel(
         @Param("pair") pair: String
        ,@Param("direction") direction: OrderDirection
        ,@Param("userLevel") userLevel: String): Mono<PairFeeConfigModel?>
}