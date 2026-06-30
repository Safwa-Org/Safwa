package com.tasneem.safwa.features.home.presentation.view.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tasneem.safwa.R
import com.tasneem.safwa.core.theme.SafwaTheme

import com.tasneem.safwa.features.home.presentation.state.GreetingType

@Composable
fun GreetingSection(
    greeting: GreetingType,
    userName: String,
    modifier: Modifier = Modifier
) {
    val greetingText = when (greeting) {
        GreetingType.MORNING -> stringResource(id = R.string.good_morning)
        GreetingType.AFTERNOON -> stringResource(id = R.string.good_afternoon)
        GreetingType.EVENING -> stringResource(id = R.string.good_evening)
    }

    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "$greetingText, $userName",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(id = R.string.home_headline),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(name = "Morning", showBackground = true)
@Composable
private fun GreetingSectionMorningPreview() {
    SafwaTheme {
        GreetingSection(
            greeting = GreetingType.MORNING,
            userName = "Ashraf"
        )
    }
}

@Preview(name = "Afternoon", showBackground = true)
@Composable
private fun GreetingSectionAfternoonPreview() {
    SafwaTheme {
        GreetingSection(
            greeting = GreetingType.AFTERNOON,
            userName = "Ashraf"
        )
    }
}

@Preview(name = "Evening", showBackground = true)
@Composable
private fun GreetingSectionEveningPreview() {
    SafwaTheme {
        GreetingSection(
            greeting = GreetingType.EVENING,
            userName = "Ashraf"
        )
    }
}
