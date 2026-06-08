package io.bashpsk.emptylibs.formatter.math

import androidx.compose.runtime.Stable
import kotlin.math.sign

enum class NumberCategory {

    Zero,
    Positive,
    Negative,
    Unknown;
}

@Stable
fun Number.numberCategory(): NumberCategory {

    return when {

        hasZero() -> NumberCategory.Zero
        hasPositive() -> NumberCategory.Positive
        hasNegative() -> NumberCategory.Negative
        else -> NumberCategory.Unknown
    }
}

@Stable
fun Number.hasPositive(): Boolean {

    return this.toDouble().sign == 1.0
}

@Stable
fun Number.hasNegative(): Boolean {

    return this.toDouble().sign == -1.0
}

@Stable
fun Number.hasZero(): Boolean {

    return this.toDouble().sign == 0.0
}