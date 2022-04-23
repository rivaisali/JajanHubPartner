package com.arajangstudio.jajanhub_partner.data.remote.exceptions

class OtherException(val msg: String): Exception() {

    override val message: String?
        get() = msg

}