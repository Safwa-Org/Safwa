package com.tasneem.safwa.features.payment.domain.usecase

import com.tasneem.safwa.features.payment.domain.model.CardDetails
import com.tasneem.safwa.features.payment.domain.model.SavedCard
import com.tasneem.safwa.features.payment.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaveCardUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    operator fun invoke(cardDetails: CardDetails): Flow<Result<SavedCard>> {
        return repository.saveNewCard(cardDetails)
    }
}
