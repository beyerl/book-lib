package com.lspace.booklib.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lspace.booklib.R

/** The L-Space librarian mascot (the original orangutan launcher art), reused in-app. */
@Composable
fun OrangutanBadge(
    modifier: Modifier = Modifier,
    sizeDp: Int = 40,
) {
    Image(
        painter = painterResource(R.drawable.ic_launcher_foreground),
        contentDescription = "L-Space librarian",
        modifier = modifier.size(sizeDp.dp).clip(CircleShape),
    )
}
