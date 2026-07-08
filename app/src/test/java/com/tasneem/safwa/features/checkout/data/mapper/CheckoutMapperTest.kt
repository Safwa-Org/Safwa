package com.tasneem.safwa.features.checkout.data.mapper

import com.tasneem.network.dto.checkout.CompletedDraftOrderDto
import com.tasneem.network.dto.checkout.DraftOrderDto
import com.tasneem.network.dto.checkout.MoneyBagDto
import com.tasneem.network.dto.checkout.MoneyDto
import com.tasneem.network.dto.checkout.OrderRefDto
import com.tasneem.safwa.features.checkout.domain.model.DraftOrderLineItem
import com.tasneem.safwa.features.checkout.domain.model.DraftOrderRequest
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Address
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CheckoutMapperTest {

    private fun address(
        street: String = "12 Nile St",
        apartment: String = "Apt 5",
        city: String = "Cairo",
        province: String = "Cairo",
        zip: String = "11511",
        countryName: String = "Egypt",
        firstName: String = "Tasneem",
        lastName: String = "Ali",
        phone: String = "+201000000000"
    ) = Address(
        street = street,
        apartment = apartment,
        city = city,
        province = province,
        zip = zip,
        countryName = countryName,
        firstName = firstName,
        lastName = lastName,
        phone = phone
    )

    private fun request(
        email: String? = "buyer@example.com",
        lineItems: List<DraftOrderLineItem> = listOf(DraftOrderLineItem("gid://variant/1", 2)),
        shippingAddress: Address = address(),
        discountAmount: Double? = null,
        discountCode: String? = null,
        shippingLineTitle: String? = null,
        shippingLineAmount: Double? = null,
        note: String? = "Placed via Safwa Android app"
    ) = DraftOrderRequest(
        email = email,
        lineItems = lineItems,
        shippingAddress = shippingAddress,
        discountAmount = discountAmount,
        discountCode = discountCode,
        shippingLineTitle = shippingLineTitle,
        shippingLineAmount = shippingLineAmount,
        note = note
    )

    @Nested
    inner class RequestToDto {

        @Test
        fun `maps line items email note and constant tags`() {
            val dto = request(
                email = "buyer@example.com",
                lineItems = listOf(
                    DraftOrderLineItem("gid://variant/1", 2),
                    DraftOrderLineItem("gid://variant/2", 1)
                ),
                note = "hello"
            ).toDto()

            assertEquals(2, dto.lineItems.size)
            assertEquals("gid://variant/1", dto.lineItems[0].variantId)
            assertEquals(2, dto.lineItems[0].quantity)
            assertEquals("buyer@example.com", dto.email)
            assertEquals("hello", dto.note)
            assertEquals(listOf("safwa-android"), dto.tags)
        }

        @Test
        fun `blank email becomes null`() {
            val dto = request(email = "   ").toDto()

            assertNull(dto.email)
        }

        @Test
        fun `null email stays null`() {
            val dto = request(email = null).toDto()

            assertNull(dto.email)
        }

        @Test
        fun `shipping and billing addresses are both mapped from shipping address`() {
            val dto = request(shippingAddress = address()).toDto()

            assertEquals("12 Nile St", dto.shippingAddress?.address1)
            assertEquals("12 Nile St", dto.billingAddress?.address1)
            assertEquals("Cairo", dto.shippingAddress?.city)
            assertEquals("Egypt", dto.shippingAddress?.country)
            assertEquals("Tasneem", dto.shippingAddress?.firstName)
        }

        @Test
        fun `blank address fields map to null`() {
            val dto = request(
                shippingAddress = address(apartment = "", province = "", phone = "")
            ).toDto()

            assertNull(dto.shippingAddress?.address2)
            assertNull(dto.shippingAddress?.province)
            assertNull(dto.shippingAddress?.phone)
            assertEquals("12 Nile St", dto.shippingAddress?.address1)
        }

        @Test
        fun `discount amount present builds applied discount block`() {
            val dto = request(discountAmount = 15.5, discountCode = "SAVE15").toDto()

            val discount = dto.appliedDiscount
            assertEquals(15.5, discount?.value)
            assertEquals("FIXED_AMOUNT", discount?.valueType)
            assertEquals("SAVE15", discount?.title)
            assertEquals("Cart coupon: SAVE15", discount?.description)
        }

        @Test
        fun `null discount amount yields null applied discount`() {
            val dto = request(discountAmount = null, discountCode = "SAVE15").toDto()

            assertNull(dto.appliedDiscount)
        }

        @Test
        fun `shipping line present formats price and keeps title`() {
            val dto = request(shippingLineTitle = "Express", shippingLineAmount = 40.0).toDto()

            assertEquals("Express", dto.shippingLine?.title)
            assertEquals("40.00", dto.shippingLine?.price)
        }

        @Test
        fun `shipping line falls back to standard title when title null`() {
            val dto = request(shippingLineTitle = null, shippingLineAmount = 9.5).toDto()

            assertEquals("Standard Shipping", dto.shippingLine?.title)
            assertEquals("9.50", dto.shippingLine?.price)
        }

        @Test
        fun `null shipping amount yields null shipping line`() {
            val dto = request(shippingLineAmount = null).toDto()

            assertNull(dto.shippingLine)
        }
    }

    @Nested
    inner class DraftOrderDtoToDomain {

        private fun money(amount: String?, currency: String? = "EGP") =
            MoneyBagDto(MoneyDto(amount = amount, currencyCode = currency))

        @Test
        fun `maps all money fields and currency`() {
            val domain = DraftOrderDto(
                id = "gid://draft/1",
                subtotalPriceSet = money("100.0"),
                totalTaxSet = money("14.0"),
                totalShippingPriceSet = money("5.0"),
                totalPriceSet = money("119.0", "EGP")
            ).toDomain()

            assertEquals("gid://draft/1", domain.id)
            assertEquals(100.0, domain.subtotal)
            assertEquals(14.0, domain.tax)
            assertEquals(5.0, domain.shipping)
            assertEquals(119.0, domain.total)
            assertEquals("EGP", domain.currency)
        }

        @Test
        fun `null money sets are null and total defaults to zero`() {
            val domain = DraftOrderDto(id = "gid://draft/2").toDomain()

            assertNull(domain.subtotal)
            assertNull(domain.tax)
            assertNull(domain.shipping)
            assertEquals(0.0, domain.total)
            assertEquals("", domain.currency)
        }

        @Test
        fun `unparseable amount becomes null`() {
            val domain = DraftOrderDto(
                id = "gid://draft/3",
                subtotalPriceSet = money("N/A"),
                totalPriceSet = money("50.0")
            ).toDomain()

            assertNull(domain.subtotal)
            assertEquals(50.0, domain.total)
        }
    }

    @Nested
    inner class CompletedDraftOrderDtoToDomain {

        @Test
        fun `maps ids and order name`() {
            val domain = CompletedDraftOrderDto(
                id = "gid://draft/9",
                order = OrderRefDto(id = "gid://order/55", name = "#1055")
            ).toDomain()

            assertEquals("gid://draft/9", domain?.draftOrderId)
            assertEquals("gid://order/55", domain?.orderId)
            assertEquals("#1055", domain?.orderName)
        }

        @Test
        fun `order name falls back to id suffix when name null`() {
            val domain = CompletedDraftOrderDto(
                id = "gid://draft/9",
                order = OrderRefDto(id = "gid://shopify/Order/55", name = null)
            ).toDomain()

            assertEquals("55", domain?.orderName)
        }

        @Test
        fun `null order yields null completed order`() {
            val domain = CompletedDraftOrderDto(id = "gid://draft/9", order = null).toDomain()

            assertNull(domain)
        }
    }
}
