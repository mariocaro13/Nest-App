package com.example.carolsnest.navigation

object AppDestinations {
    const val HOME_SCREEN = "home"
    const val LOGIN_SCREEN = "login"
    const val SIGNUP_SCREEN = "signup"
    const val PROFILE_SCREEN = "profile"

    const val BIRD_DETAIL_BASE = "birdDetail"
    const val BIRD_ID_ARG = "birdId"
    const val BIRD_DETAIL_ROUTE = "$BIRD_DETAIL_BASE/{$BIRD_ID_ARG}"
}