package com.rajames.forth.runtime

class ForthException extends Exception {

    ForthException() {
        super()
    }

    ForthException(String message) {
        super(message)
    }

    ForthException(String message, Throwable cause) {
        super(message, cause)
    }

    ForthException(Throwable cause) {
        super(cause)
    }

}
