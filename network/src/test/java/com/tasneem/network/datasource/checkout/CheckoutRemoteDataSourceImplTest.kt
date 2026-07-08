package com.tasneem.network.datasource.checkout

import com.google.android.gms.tasks.Tasks
import com.google.firebase.appcheck.AppCheckToken
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import com.tasneem.network.dto.checkout.CompletedDraftOrderDto
import com.tasneem.network.dto.checkout.DraftOrderCompleteDataDto
import com.tasneem.network.dto.checkout.DraftOrderCompletePayloadDto
import com.tasneem.network.dto.checkout.DraftOrderCreateDataDto
import com.tasneem.network.dto.checkout.DraftOrderCreatePayloadDto
import com.tasneem.network.dto.checkout.DraftOrderDto
import com.tasneem.network.dto.checkout.GraphQLErrorDto
import com.tasneem.network.dto.checkout.GraphQLResponseDto
import com.tasneem.network.dto.checkout.OrderCancelDataDto
import com.tasneem.network.dto.checkout.OrderCancelPayloadDto
import com.tasneem.network.dto.checkout.OrderMarkAsPaidDataDto
import com.tasneem.network.dto.checkout.OrderMarkAsPaidPayloadDto
import com.tasneem.network.dto.checkout.OrderRefDto
import com.tasneem.network.dto.checkout.UserErrorDto
import com.tasneem.network.exception.EmptyResponseException
import com.tasneem.network.exception.GraphQlException
import com.tasneem.network.exception.NetworkException
import com.tasneem.network.exception.UnauthenticatedException
import com.tasneem.network.exception.UnknownException
import com.tasneem.network.exception.UserErrorException
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response

class CheckoutRemoteDataSourceImplTest {

    private val adminProxyApi = mockk<AdminProxyApi>()
    private val firebaseAuth = mockk<FirebaseAuth>()
    private val appCheck = mockk<FirebaseAppCheck>()

    private val dataSource = CheckoutRemoteDataSourceImpl(adminProxyApi, firebaseAuth, appCheck)

    @BeforeEach
    fun stubFirebaseHeaders() {
        val user = mockk<FirebaseUser>()
        val tokenResult = mockk<GetTokenResult> { every { token } returns "id-token" }
        every { firebaseAuth.currentUser } returns user
        every { user.getIdToken(false) } returns Tasks.forResult(tokenResult)
        val appCheckToken = mockk<AppCheckToken> { every { token } returns "appcheck-token" }
        every { appCheck.getAppCheckToken(false) } returns Tasks.forResult(appCheckToken)
    }

    // ---------- createDraftOrder ----------

    @Test
    fun `createDraftOrder returns draft order on success`() = runTest {
        val draft = DraftOrderDto(id = "gid://draft/1")
        coEvery { adminProxyApi.draftOrderCreate(any(), any(), any()) } returns
            GraphQLResponseDto(data = DraftOrderCreateDataDto(DraftOrderCreatePayloadDto(draftOrder = draft)))

        val result = dataSource.createDraftOrder(mockk(relaxed = true))

        assertEquals("gid://draft/1", result.id)
    }

    @Test
    fun `createDraftOrder throws GraphQlException when response has graphql errors`() = runTest {
        coEvery { adminProxyApi.draftOrderCreate(any(), any(), any()) } returns
            GraphQLResponseDto(data = null, errors = listOf(GraphQLErrorDto("bad input")))

        val thrown = runCatching { dataSource.createDraftOrder(mockk(relaxed = true)) }.exceptionOrNull()

        assertTrue(thrown is GraphQlException)
        assertEquals("bad input", (thrown as GraphQlException).errors)
    }

    @Test
    fun `createDraftOrder throws EmptyResponseException when data is null`() = runTest {
        coEvery { adminProxyApi.draftOrderCreate(any(), any(), any()) } returns
            GraphQLResponseDto(data = null)

        val thrown = runCatching { dataSource.createDraftOrder(mockk(relaxed = true)) }.exceptionOrNull()

        assertTrue(thrown is EmptyResponseException)
    }

    @Test
    fun `createDraftOrder throws EmptyResponseException when payload is null`() = runTest {
        coEvery { adminProxyApi.draftOrderCreate(any(), any(), any()) } returns
            GraphQLResponseDto(data = DraftOrderCreateDataDto(draftOrderCreate = null))

        val thrown = runCatching { dataSource.createDraftOrder(mockk(relaxed = true)) }.exceptionOrNull()

        assertTrue(thrown is EmptyResponseException)
    }

    @Test
    fun `createDraftOrder throws UserErrorException when payload has user errors`() = runTest {
        coEvery { adminProxyApi.draftOrderCreate(any(), any(), any()) } returns
            GraphQLResponseDto(
                data = DraftOrderCreateDataDto(
                    DraftOrderCreatePayloadDto(
                        draftOrder = null,
                        userErrors = listOf(UserErrorDto(message = "Line invalid"))
                    )
                )
            )

        val thrown = runCatching { dataSource.createDraftOrder(mockk(relaxed = true)) }.exceptionOrNull()

        assertTrue(thrown is UserErrorException)
        assertEquals("Line invalid", (thrown as UserErrorException).messages.single())
    }

    @Test
    fun `createDraftOrder throws EmptyResponseException when draft order is null without user errors`() = runTest {
        coEvery { adminProxyApi.draftOrderCreate(any(), any(), any()) } returns
            GraphQLResponseDto(data = DraftOrderCreateDataDto(DraftOrderCreatePayloadDto(draftOrder = null)))

        val thrown = runCatching { dataSource.createDraftOrder(mockk(relaxed = true)) }.exceptionOrNull()

        assertTrue(thrown is EmptyResponseException)
    }

    @Test
    fun `createDraftOrder maps HttpException to NetworkException`() = runTest {
        val http = HttpException(Response.error<Any>(500, "err".toResponseBody(null)))
        coEvery { adminProxyApi.draftOrderCreate(any(), any(), any()) } throws http

        val thrown = runCatching { dataSource.createDraftOrder(mockk(relaxed = true)) }.exceptionOrNull()

        assertTrue(thrown is NetworkException)
    }

    @Test
    fun `createDraftOrder maps generic exception to UnknownException`() = runTest {
        coEvery { adminProxyApi.draftOrderCreate(any(), any(), any()) } throws IllegalStateException("boom")

        val thrown = runCatching { dataSource.createDraftOrder(mockk(relaxed = true)) }.exceptionOrNull()

        assertTrue(thrown is UnknownException)
    }

    @Test
    fun `createDraftOrder throws UnauthenticatedException when there is no signed-in user`() = runTest {
        every { firebaseAuth.currentUser } returns null
        coEvery { adminProxyApi.draftOrderCreate(any(), any(), any()) } returns
            GraphQLResponseDto(data = DraftOrderCreateDataDto(DraftOrderCreatePayloadDto(draftOrder = DraftOrderDto("x"))))

        val thrown = runCatching { dataSource.createDraftOrder(mockk(relaxed = true)) }.exceptionOrNull()

        assertTrue(thrown is UnauthenticatedException)
    }

    // ---------- completeDraftOrder ----------

    @Test
    fun `completeDraftOrder returns completed draft order on success`() = runTest {
        val completed = CompletedDraftOrderDto(id = "gid://draft/1", order = OrderRefDto("gid://order/9", "#1009"))
        coEvery { adminProxyApi.draftOrderComplete(any(), any(), any()) } returns
            GraphQLResponseDto(data = DraftOrderCompleteDataDto(DraftOrderCompletePayloadDto(draftOrder = completed)))

        val result = dataSource.completeDraftOrder("gid://draft/1", paymentPending = true)

        assertEquals("gid://order/9", result.order?.id)
    }

    @Test
    fun `completeDraftOrder throws UserErrorException on user errors`() = runTest {
        coEvery { adminProxyApi.draftOrderComplete(any(), any(), any()) } returns
            GraphQLResponseDto(
                data = DraftOrderCompleteDataDto(
                    DraftOrderCompletePayloadDto(
                        draftOrder = null,
                        userErrors = listOf(UserErrorDto(message = "Cannot complete"))
                    )
                )
            )

        val thrown = runCatching { dataSource.completeDraftOrder("gid://draft/1", true) }.exceptionOrNull()

        assertTrue(thrown is UserErrorException)
    }

    // ---------- markOrderAsPaid ----------

    @Test
    fun `markOrderAsPaid succeeds when there are no user errors`() = runTest {
        coEvery { adminProxyApi.orderMarkAsPaid(any(), any(), any()) } returns
            GraphQLResponseDto(data = OrderMarkAsPaidDataDto(OrderMarkAsPaidPayloadDto(order = OrderRefDto("gid://order/9"))))

        // Should not throw
        dataSource.markOrderAsPaid("gid://order/9")
    }

    @Test
    fun `markOrderAsPaid throws UserErrorException on user errors`() = runTest {
        coEvery { adminProxyApi.orderMarkAsPaid(any(), any(), any()) } returns
            GraphQLResponseDto(
                data = OrderMarkAsPaidDataDto(
                    OrderMarkAsPaidPayloadDto(userErrors = listOf(UserErrorDto(message = "Already paid")))
                )
            )

        val thrown = runCatching { dataSource.markOrderAsPaid("gid://order/9") }.exceptionOrNull()

        assertTrue(thrown is UserErrorException)
    }

    // ---------- cancelOrder ----------

    @Test
    fun `cancelOrder succeeds when there are no user errors`() = runTest {
        coEvery { adminProxyApi.orderCancel(any(), any(), any()) } returns
            GraphQLResponseDto(data = OrderCancelDataDto(OrderCancelPayloadDto()))

        // Should not throw
        dataSource.cancelOrder("gid://order/9", notifyCustomer = false)
    }

    @Test
    fun `cancelOrder throws UserErrorException from orderCancelUserErrors`() = runTest {
        coEvery { adminProxyApi.orderCancel(any(), any(), any()) } returns
            GraphQLResponseDto(
                data = OrderCancelDataDto(
                    OrderCancelPayloadDto(orderCancelUserErrors = listOf(UserErrorDto(message = "Cannot cancel")))
                )
            )

        val thrown = runCatching { dataSource.cancelOrder("gid://order/9", false) }.exceptionOrNull()

        assertTrue(thrown is UserErrorException)
        assertEquals("Cannot cancel", (thrown as UserErrorException).messages.single())
    }
}
