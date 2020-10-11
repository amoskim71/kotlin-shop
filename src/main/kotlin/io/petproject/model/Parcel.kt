package io.petproject.model

import java.math.BigDecimal
import java.math.RoundingMode

data class Parcel(val items: List<Item>,
                  val shippingAddress: Address,
                  val shippingLabel: ShippingLabel) {

    companion object {

        fun of(items: List<Item>, shippingAddress: Address): List<Parcel> {
            TODO("implement parsing of items into parcels")
        }

        fun shippingCostsOf(parcels: List<Parcel>): BigDecimal {
            return parcels.map { it.shippingAndHandlingCosts() }
                    .fold(BigDecimal.ZERO) { acc, value -> acc.plus(value) }
                    .setScale(2, RoundingMode.HALF_UP)
        }

        fun importationFeesOf(parcels: List<Parcel>): BigDecimal {
            return parcels.map { it.importationTaxesAndFees() }
                    .fold(BigDecimal.ZERO) { acc, value -> acc.plus(value) }
                    .setScale(2, RoundingMode.HALF_UP)
        }
    }

    fun shippingAndHandlingCosts(): BigDecimal {
        //TODO("compute logistics costs based on size, weight and shippingAddress")
        return BigDecimal.TEN
    }

    fun importationTaxesAndFees(): BigDecimal {
        //TODO("compute taxes for importation in the destination country")
        return BigDecimal.ZERO
    }

}

enum class ShippingLabel(val description: String = "") {
    DEFAULT,
    TAX_FREE
}