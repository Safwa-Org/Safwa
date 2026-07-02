package com.tasneem.safwa.features.payment.data.datasource.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tasneem.safwa.features.payment.domain.model.SavedCard

@Entity(tableName = "saved_cards_table")
data class SavedCardEntity(
    @PrimaryKey
    val id: String,
    val type: String,
    val last4: String,
    val cardholderName: String,
    val expiryDate: String,
    val isDefault: Boolean
) {
    fun toDomainModel(): SavedCard {
        return SavedCard(
            id = id,
            type = type,
            last4 = last4,
            cardholderName = cardholderName,
            expiryDate = expiryDate,
            isDefault = isDefault
        )
    }
}

fun SavedCard.toEntity(): SavedCardEntity {
    return SavedCardEntity(
        id = id,
        type = type,
        last4 = last4,
        cardholderName = cardholderName,
        expiryDate = expiryDate,
        isDefault = isDefault
    )
}
