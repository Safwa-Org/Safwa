package com.tasneem.safwa.features.home.presentation.view.component

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.tasneem.safwa.R
import com.tasneem.safwa.core.theme.SafwaTheme
import com.tasneem.safwa.features.home.domain.model.PromoBanner
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun PromoBannerPager(
    banners: List<PromoBanner>,
    modifier: Modifier = Modifier
) {
    if (banners.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { banners.size })

    LaunchedEffect(pagerState) {
        while (true) {
            delay(8000.milliseconds)
            val nextPage = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 12.dp
        ) { page ->
            PromoBannerCard(banner = banners[page])
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(banners.size) { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == pagerState.currentPage) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == pagerState.currentPage)
                                MaterialTheme.colorScheme.onBackground
                            else
                                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f)
                        )
                )
            }
        }
    }
}

@Composable
private fun PromoBannerCard(
    banner: PromoBanner,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(210.dp)
            .clickable { copyToClipboard(context, banner.promoCode) },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = banner.backgroundColor)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = 20.dp, top = 16.dp, bottom = 16.dp, end = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = banner.contentColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = stringResource(id = banner.tagLabel),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = banner.contentColor
                    )
                }

                Text(
                    text = stringResource(id = banner.headline),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = banner.contentColor,
                    lineHeight = 22.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = banner.contentColor.copy(alpha = 0.08f),
                    border = BorderStroke(
                        1.dp,
                        banner.contentColor.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.promo_code_label),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 8.sp,
                            color = banner.contentColor.copy(alpha = 0.7f)
                        )
                        Text(
                            text = banner.promoCode,
                            style = MaterialTheme.typography.labelMedium,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = banner.contentColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            AsyncImage(
                model = banner.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(0.75f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp))
            )
        }
    }
}

private fun copyToClipboard(context: Context, code: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("promo_code", code)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, context.getString(R.string.code_copied), Toast.LENGTH_SHORT).show()
}

@Preview(showBackground = true)
@Composable
private fun PromoBannerPagerPreview() {
    SafwaTheme {
        PromoBannerPager(
            banners = listOf(
                PromoBanner(
                    id = "1",
                    tagLabel = R.string.promo_black_friday_tag,
                    headline = R.string.promo_black_friday_headline,
                    promoCode = "NOIR200",
                    imageUrl = "https://images.unsplash.com/photo-1524592094714-0f0654e20314",
                    backgroundColor = Color(0xFF1A1A1A),
                    contentColor = Color.White
                ),
                PromoBanner(
                    id = "2",
                    tagLabel = R.string.promo_free_shipping_tag,
                    headline = R.string.promo_free_shipping_headline,
                    promoCode = "LUMIERE15",
                    imageUrl = "https://images.unsplash.com/photo-1511499767150-a48a237f0083",
                    backgroundColor = Color(0xFFE8C874),
                    contentColor = Color(0xFF1A1A1A)
                ),
                PromoBanner(
                    id = "3",
                    tagLabel = R.string.promo_half_price_tag,
                    headline = R.string.promo_half_price_headline,
                    promoCode = "ELITE10",
                    imageUrl = "https://images.unsplash.com/photo-1541643600914-78b084683601",
                    backgroundColor = Color(0xFF005D39),
                    contentColor = Color.White
                )
            ),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
