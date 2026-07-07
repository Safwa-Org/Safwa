package com.tasneem.safwa.features.settings.orderhistory.data.repository

import com.tasneem.network.datasource.order.OrderRemoteDataSource
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.settings.orderhistory.data.datasource.local.OrderDao
import com.tasneem.safwa.features.settings.orderhistory.data.datasource.local.toDomain
import com.tasneem.safwa.features.settings.orderhistory.data.datasource.local.toEntity
import com.tasneem.safwa.features.settings.orderhistory.domain.model.OrderHistoryItem
import com.tasneem.safwa.features.settings.orderhistory.domain.model.OrderStatus
import com.tasneem.safwa.features.settings.orderhistory.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val remoteDataSource: OrderRemoteDataSource,
    private val localDataSource: OrderDao
) : OrderRepository {
    override fun getOrders(customerAccessToken: String): Flow<Resource<List<OrderHistoryItem>>> = flow {
        emit(Resource.Loading)

        var remoteError: Exception? = null
        try {
            val edges = remoteDataSource.getOrders(customerAccessToken)
            val remoteOrders = edges.mapNotNull { it.node }.map { node ->
                
                val status = when {
                    node.canceledAt != null -> OrderStatus.CANCELLED
                    node.fulfillmentStatus.name == "FULFILLED" -> OrderStatus.DELIVERED
                    else -> OrderStatus.IN_TRANSIT
                }

                val date = node.processedAt.toString().take(10)

                val lineItems = node.lineItems.edges.map { edge ->
                    com.tasneem.safwa.features.settings.orderhistory.domain.model.OrderLineItem(
                        title = edge.node.title,
                        quantity = edge.node.quantity,
                        imageUrl = edge.node.variant?.image?.url?.toString()
                    )
                }

                OrderHistoryItem(
                    id = node.id,
                    orderNumber = node.orderNumber.toString(),
                    status = status,
                    lineItems = lineItems,
                    itemCount = node.lineItems.edges.sumOf { it.node.quantity },
                    date = date,
                    totalPrice = "${node.currentTotalPrice.currencyCode} ${node.currentTotalPrice.amount}"
                )
            }

            localDataSource.deleteAllOrders()
            localDataSource.insertOrders(remoteOrders.map { it.toEntity() })
        } catch (e: Exception) {
            remoteError = e
        }

        emitAll(
            localDataSource.getOrders().map { entities ->
                if (entities.isEmpty() && remoteError != null) {
                    Resource.Error(remoteError.message ?: "An unknown error occurred")
                } else {
                    Resource.Success(entities.map { it.toDomain() })
                }
            }
        )
    }

    override fun getOrderById(orderId: String): Flow<OrderHistoryItem?> {
        return localDataSource.getOrderById(orderId).map { it?.toDomain() }
    }

    override suspend fun clearOrders() {
        localDataSource.deleteAllOrders()
    }

    override suspend fun markOrderCancelled(orderId: String) {
        localDataSource.updateStatus(orderId, OrderStatus.CANCELLED.name)
    }
}
