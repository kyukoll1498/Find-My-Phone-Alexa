package com.mtg.tool.findmyphone.main.activity.jpcompose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.mtg.tool.findmyphone.R

@Composable
fun HowToUseScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        HowToUseItem(
            icon = painterResource(id = R.drawable.ic_guide_active),
            title = "1. " + stringResource(id = R.string.activate),
            description = stringResource(id = R.string.click_the_activate_button),
            backgroundColor = Color(0xFFF5F9FF)
        )

        HowToUseItem(
            icon = painterResource(id = R.drawable.ic_guide_clap),
            title = "2. " + stringResource(id = R.string.clap),
            description = stringResource(id = R.string.double_clap_to_find_your_phone),
            backgroundColor = Color(0xFFF5F9FF)
        )

        HowToUseItem(
            icon = painterResource(id = R.drawable.ic_guide_detect),
            title = "3. " + stringResource(id = R.string.detect),
            description = stringResource(id = R.string.app_detect_the_clap_and_start_ringing),
            backgroundColor = Color(0xFFF5F9FF)
        )

        HowToUseItem(
            icon = painterResource(id = R.drawable.ic_guide_settings),
            title = "4. " + stringResource(id = R.string.txt_settings),
            description = stringResource(id = R.string.before_activating_set_flashlight_adjust_volume),
            backgroundColor = Color(0xFFF5F9FF)
        )

        HowToUseItem(
            icon = painterResource(id = R.drawable.ic_guide_pms),
            title = "5. " + stringResource(id = R.string.permission),
            description = stringResource(id = R.string.allow_permission_to_access_microphone_audio_files),
            backgroundColor = Color(0xFFF5F9FF)
        )
    }
}

@Composable
fun HowToUseItem(
    icon: Painter,
    title: String,
    description: String,
    backgroundColor: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(55.dp)
                    .padding(end = 16.dp)
            )
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontFamily = FontFamily(Font(R.font.poppins_medium)),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.poppins_regular))
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHowToUseScreen() {
    HowToUseScreen()
}
