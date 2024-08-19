package io.github.estivensh.sample

sealed class Router(val route: String) {
    data object Camera : Router("camera")
    data object Gallery : Router("gallery")
}

/*
fun Navigator.navigated(route: Router) {
    navigate(
        route = route.route,
        options = NavOptions(
            popUpTo = PopUpTo(
                route = route.route,
                inclusive = true
            )
        )
    )
}*/
