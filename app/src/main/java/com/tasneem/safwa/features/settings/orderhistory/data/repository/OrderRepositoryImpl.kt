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
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val remoteDataSource: OrderRemoteDataSource,
    private val localDataSource: OrderDao
) : OrderRepository {
    override fun getOrders(customerAccessToken: String): Flow<Resource<List<OrderHistoryItem>>> = flow {
        emit(Resource.Loading)

        val localOrders = localDataSource.getOrders().first()
        if (localOrders.isNotEmpty()) {
            emit(Resource.Success(localOrders.map { it.toDomain() }))
        }

        try {
            val edges = remoteDataSource.getOrders(customerAccessToken)
            val remoteOrders = edges.mapNotNull { it.node }.map { node ->
                
                val status = when {
                    node.fulfillmentStatus.name == "FULFILLED" -> OrderStatus.DELIVERED
                    node.financialStatus?.name == "REFUNDED" -> OrderStatus.CANCELLED
                    else -> OrderStatus.IN_TRANSIT
                }

                val date = node.processedAt.toString().take(10) 
                
                val images = node.lineItems.edges.mapNotNull { it.node.variant?.image?.url?.toString() }
                
                OrderHistoryItem(
                    id = node.id,
                    orderNumber = node.orderNumber.toString(),
                    status = status,
                    images = images,
                    itemCount = node.lineItems.edges.sumOf { it.node.quantity },
                    date = date,
                    totalPrice = "${node.currentTotalPrice.currencyCode} ${node.currentTotalPrice.amount}"
                )
            }

            localDataSource.deleteAllOrders()
            localDataSource.insertOrders(remoteOrders.map { it.toEntity() })

            emit(Resource.Success(remoteOrders))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unknown error occurred"))
        }
    }
}
