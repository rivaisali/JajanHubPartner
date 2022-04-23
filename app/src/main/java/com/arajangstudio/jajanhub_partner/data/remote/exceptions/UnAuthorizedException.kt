package com.arajangstudio.jajanhub_partner.data.remote.exceptions

import java.io.IOException

class UnAuthorizedException : IOException() {

    override val message: String?
        get() = "User Unauthorized"
}