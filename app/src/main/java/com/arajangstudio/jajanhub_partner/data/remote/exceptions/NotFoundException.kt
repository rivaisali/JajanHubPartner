package com.arajangstudio.jajanhub_partner.data.remote.exceptions

import java.io.IOException

class NotFoundException : IOException() {

    override val message: String?
        get() = "Not Found"
}