package com.etrak.shared_library.ui.theme

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(0.dp)
)

val dialogShape = CutCornerShape(size = 15.dp)
val buttonShape = CutCornerShape(
    topStartPercent = 25,
    topEndPercent = 25,
    bottomEndPercent = 0,
    bottomStartPercent = 0
)