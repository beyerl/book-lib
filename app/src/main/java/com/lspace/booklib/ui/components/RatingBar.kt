package com.lspace.booklib.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lspace.booklib.ui.theme.StarGold

/** A 1–5 star rating. When [onRatingChange] is provided the stars are tappable. */
@Composable
fun RatingBar(
    rating: Int?,
    modifier: Modifier = Modifier,
    starSize: Int = 24,
    onRatingChange: ((Int) -> Unit)? = null,
) {
    val current = rating ?: 0
    Row(modifier = modifier) {
        for (star in 1..5) {
            val filled = star <= current
            val icon = if (filled) Icons.Filled.Star else Icons.Outlined.StarBorder
            val tint = if (filled) StarGold else Color.Gray
            if (onRatingChange != null) {
                IconButton(
                    onClick = { onRatingChange(if (current == star) 0 else star) },
                    modifier = Modifier.size((starSize + 12).dp),
                ) {
                    Icon(icon, contentDescription = "Rate $star", tint = tint, modifier = Modifier.size(starSize.dp))
                }
            } else {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(starSize.dp).clip(CircleShape),
                )
            }
        }
    }
}
