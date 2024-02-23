package extensions

val Int.minutes get() = (this / 60).toString().padStart(2, '0')

val Int.seconds get() = (this % 60).toString().padStart(2, '0')

internal fun Float.roundTo(n: Int): Float {
    return try {
        0f
        // "%.${n}f".format(Locale.US, this).toFloat()
    } catch (e: NumberFormatException) {
        this
    }
}