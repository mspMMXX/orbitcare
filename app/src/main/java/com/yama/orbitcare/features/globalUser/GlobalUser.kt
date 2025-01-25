package com.yama.orbitcare.features.globalUser

import com.yama.orbitcare.data.models.Employee

/**
 * A singleton object to manage the global user state.
 * Holds the currently logged-in user's information throughout the app's lifecycle.
 */
object GlobalUser {

    /**
     * The currently logged-in employee.
     * This is set during login and can be accessed globally within the app.
     * If no user is logged in, this value will be `null`.
     */
    var currentUser: Employee? = null
}