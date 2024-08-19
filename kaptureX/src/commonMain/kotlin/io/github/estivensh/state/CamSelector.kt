package io.github.estivensh.state

expect enum class CamSelector {
    Front,
    Back;

    val inverse: CamSelector
}