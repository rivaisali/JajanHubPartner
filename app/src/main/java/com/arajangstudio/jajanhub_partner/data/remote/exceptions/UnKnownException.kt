package com.arajangstudio.jajanhub_partner.data.remote.exceptions

class UnKnownException : Exception() {

    override val message: String?
        get() = "Some Unknown Error Occurred"
}