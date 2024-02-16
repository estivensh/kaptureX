package state

expect enum class CamSelector {
    Front,
    Back;

    val inverse: CamSelector
}