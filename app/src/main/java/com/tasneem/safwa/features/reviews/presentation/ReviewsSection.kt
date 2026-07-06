package com.tasneem.safwa.features.reviews.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tasneem.safwa.R
import com.tasneem.safwa.features.reviews.domain.model.Review
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReviewsSection(
    reviews: List<Review>,
    currentUserId: String?,
    newReviewRating: Int,
    newReviewComment: String,
    isSubmittingReview: Boolean,
    onRatingChanged: (Int) -> Unit,
    onCommentChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val averageRating = if (reviews.isNotEmpty()) reviews.map { it.rating }.average() else 0.0
    val existingReviewFromUser = reviews.firstOrNull { it.userId == currentUserId }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.reviews),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (reviews.isNotEmpty()) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "%.1f".format(averageRating) + " (${reviews.size})",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (existingReviewFromUser != null) stringResource(R.string.update_your_review)
                else stringResource(R.string.write_a_review),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row {
                for (star in 1..5) {
                    IconButton(onClick = { onRatingChanged(star) }) {
                        Icon(
                            imageVector = if (star <= newReviewRating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            OutlinedTextField(
                value = newReviewComment,
                onValueChange = onCommentChanged,
                label = { Text(stringResource(R.string.share_your_thoughts_about_this_product)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = RoundedCornerShape(16.dp)
            )

            Button(
                onClick = onSubmit,
                enabled = !isSubmittingReview,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSubmittingReview) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text(if (existingReviewFromUser != null) stringResource(R.string.update_review)
                    else stringResource(R.string.submit_review))
                }
            }
        }

        if (reviews.isEmpty()) {
            Text(
                text = stringResource(R.string.no_reviews_yet),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                reviews.forEach { review ->
                    ReviewItem(review)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun ReviewItem(review: Review) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = review.userName,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = formatDate(review.createdAt),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Row {
            repeat(5) { index ->
                Icon(
                    imageVector = if (index < review.rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Text(
            text = review.comment,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

private fun formatDate(millis: Long): String =
    SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(millis))