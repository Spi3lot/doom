package org.spi3lot.input

/**
 *  @since 03.09.2024, Di.
 *  @author Emilio Zottel
 */
class KeyHandler {

    private val pressedKeyCodes = hashSetOf<Int>()

    private val keyCodeActions = hashMapOf<Int, () -> Unit>()

    fun addKeyAction(key: Char, action: () -> Unit) {
        addKeyCodeAction(key.code, action)
    }

    fun addKeyCodeAction(keyCode: Int, action: () -> Unit) {
        keyCodeActions[keyCode] = action
    }

    fun invokeActions() {
        for (keyCode in pressedKeyCodes) {
            keyCodeActions[keyCode]?.invoke()
        }
    }

    fun keyPressed(keyCode: Int) {
        pressedKeyCodes.add(keyCode)
    }

    fun keyReleased(keyCode: Int) {
        pressedKeyCodes.remove(keyCode)
    }

}