package com.exory550.exorypad.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.geometry.Offset

private const val VIEW = "View"
private const val EDIT = "Edit"

sealed interface NavState {
    object Empty: NavState
    data class View(val id: Long): NavState
    data class Edit(val id: Long? = null, val offset: Offset?= null): NavState
}

val navStateSaver = Saver<MutableState<NavState>, Pair<String, Long?>>(
    save = {
        when(val state = it.value) {
            is NavState.View -> VIEW to state.id
            is NavState.Edit -> EDIT to state.id
            else -> "" to null
        }
    },
    restore = { (key, id) ->
        mutableStateOf(
            when(key) {
                VIEW -> NavState.View(id ?: 0)
                EDIT -> NavState.Edit(id)
                else -> NavState.Empty
            }
        )
    }
)
