package io.petproject.model

import io.petproject.model.ProductType.*
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import java.math.RoundingMode

class ShoppingCart {

    val items = HashMap<Product, Item>()
    val subtotal: BigDecimal by lazy {
        items.values
                .asSequence()
                .map(Item::subtotal)
                .fold(BigDecimal.ZERO) { acc, value -> acc.plus(value) }
                .setScale(2, RoundingMode.HALF_UP)
    }

    fun add(product: Product, quantity: Int) = apply {
        items.compute(product) { _, item ->
            item?.addMore(quantity) ?:
            Item(product, quantity)
        }
    }

    fun updateQuantity(product: Product, quantity: Int) = apply {
        if (quantity == 0) {
            delete(product)
        } else {
            items.compute(product) { _, item ->
                item?.updateTo(quantity) ?:
                throw IllegalArgumentException("Product specified is not in the Cart")
            }
        }
    }

    fun delete(product: Product) = apply {
        if (items.containsKey(product)) items.remove(product)
        else throw IllegalArgumentException("Product specified is not in the Cart")
    }

    fun checkout(account: Account): List<Order> {
        return items
            .values
            .groupBy { it.product.type }
            .map { (type, items) ->
                when (type) {
                    PHYSICAL, PHYSICAL_TAX_FREE -> listOf(PhysicalOrder(items, account))
                    DIGITAL -> listOf(DigitalOrder(items, account))
                    SUBSCRIPTION -> items.map { SubscriptionOrder(it, account) }
                }
            }
            .flatten()
    }
}
