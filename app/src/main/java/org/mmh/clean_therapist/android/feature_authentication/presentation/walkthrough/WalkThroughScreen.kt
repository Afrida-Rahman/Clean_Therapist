package org.mmh.clean_therapist.android.feature_authentication.presentation.walkthrough

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import org.mmh.clean_therapist.R
import org.mmh.clean_therapist.android.core.util.Screen
import org.mmh.clean_therapist.android.feature_authentication.domain.model.WalkThroughPage
import org.mmh.clean_therapist.android.feature_authentication.presentation.walkthrough.components.Page

@OptIn(ExperimentalPagerApi::class)
@Composable
fun WalkThroughScreen(
    navController: NavController,
    viewModel: WalkThroughScreenViewModel = hiltViewModel()
) {
    val pages = listOf(
        WalkThroughPage(
            image = R.drawable.walkthrough,
            title = "Providing a revolutionary approach to musculoskeletal",
            description = "Our health technology solution enables physicians to identify patient risk for injuring any of the ove 350 joints in the human body"
        ),
        WalkThroughPage(
            image = R.drawable.walkthrough_two,
            title = "Patient Experience Journey",
            description = "Fysical Score™ Assessment identifies and quantifies risk factors that are predisposing people to catastrophic injuries.."
        ),
        WalkThroughPage(
            image = R.drawable.walkthrough_three,
            title = "AI-powered, ever-leaning engine to beat",
            description = "My Medical Hub is a cloud-based, predictive healthcare informatics and technology health/wellness services company."
        )
    )
    val pagerState = rememberPagerState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 22.dp)

    ) {
        HorizontalPager(
            count = pages.size,
            state = pagerState,
            modifier = Modifier.weight(10f)
        ) { index ->
            Page(page = pages[index])
        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            activeColor = MaterialTheme.colors.primary,
            indicatorShape = RoundedCornerShape(3.dp),
            indicatorWidth = 60.dp,
            indicatorHeight = 6.dp,
            modifier = Modifier.weight(1f)
        )
        AnimatedVisibility(visible = pagerState.currentPage == pages.size - 1) {
            Button(
                onClick = {
                    viewModel.onEvent(WalkThroughScreenEvent.Finish)
                    navController.popBackStack()
                    navController.navigate(Screen.WelcomeScreen.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Text(text = "Finish", modifier = Modifier.padding(8.dp))
            }
        }
    }
}