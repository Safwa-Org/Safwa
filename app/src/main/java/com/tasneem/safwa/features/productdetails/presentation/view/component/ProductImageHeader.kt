package com.tasneem.safwa.features.productdetails.presentation.view.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.tasneem.safwa.R
import com.tasneem.safwa.core.theme.SafwaTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductImageHeader(
    productImages: List<String>,
    imageAltTexts: List<String>,
    title: String,
    isWishlisted: Boolean,
    onBack: () -> Unit,
    onWishlistToggle: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { productImages.size.coerceAtLeast(1) })

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(288.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        if (productImages.isNotEmpty()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                AsyncImage(
                    model = productImages[page],
                    contentDescription = imageAltTexts.getOrNull(page) ?: title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderIconButton(
                onClick = onBack,
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HeaderIconButton(
                    onClick = onShare,
                    imageVector = Icons.Default.Share,
                    contentDescription = stringResource(R.string.share)
                )

                HeaderIconButton(
                    onClick = onWishlistToggle,
                    imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(R.string.wishlist),
                    iconTint = if (isWishlisted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pagerState.pageCount) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 20.dp else 6.dp, height = 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                )
            }
        }
    }
}

@Composable
private fun HeaderIconButton(
    onClick: () -> Unit,
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.onSurface
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        modifier = modifier.size(40.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview
@Composable
private fun ProductImageHeaderPreview() {
    SafwaTheme {
        ProductImageHeader(
            productImages = listOf("https://fastly.picsum.photos/id/200/600/600.jpg?hmac=5vWrMNBeBRddgj3sQ0cVlgNer86pzuPajuJbhPodX_s"),
            imageAltTexts = listOf("Front view"),
            title = "Nike Air Max 270",
            isWishlisted = true,
            onBack = {},
            onWishlistToggle = {},
            onShare = {}
        )
    }
}