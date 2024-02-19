import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.PopUpTo

sealed class Router(val route: String) {
    object Camera : Router("camera")
    object Gallery : Router("gallery")
    object Preview : Router("preview/{${Args.Path}}") {
        fun createRoute(path: String) = "preview/$path"
    }
}

object Args {
    const val Path = "path"
}


fun Navigator.navigater(route: Router) {
    navigate(
        route = route.route,
        options = NavOptions(
            popUpTo = PopUpTo(
                route = route.route,
                inclusive = true
            )
        )
    )
}