package com.exory550.exorypad.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import com.exory550.exorypad.R

@Composable
fun BackButton(onClick: () -> Unit = {}) {
    val imageVector = when(LocalLayoutDirection.current) {
        LayoutDirection.Rtl -> Icons.AutoMirrored.Filled.ArrowForward
        else -> Icons.AutoMirrored.Filled.ArrowBack
    }

    IconButton(onClick = onClick) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = Color.White
        )
    }
}

@Composable
fun EditButton(onClick: () -> Unit = {}) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Filled.Edit,
            contentDescription = stringResource(R.string.action_edit),
            tint = Color.White
        )
    }
}

@Composable
fun SaveButton(onClick: () -> Unit = {}) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Filled.Save,
            contentDescription = stringResource(R.string.action_save),
            tint = Color.White
        )
    }
}

@Composable
fun DeleteButton(onClick: () -> Unit = {}) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = stringResource(R.string.action_delete),
            tint = Color.White
        )
    }
}

@Composable
fun MoreButton(onClick: () -> Unit = {}) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = null,
            tint = Color.White
        )
    }
}

@Composable
fun MultiSelectButton(onClick: () -> Unit = {}) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.PlaylistAddCheck,
            contentDescription = stringResource(R.string.action_start_selection),
            tint = Color.White
        )
    }
}

@Composable
fun SelectAllButton(onClick: () -> Unit = {}) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Filled.SelectAll,
            contentDescription = stringResource(R.string.action_select_all),
            tint = Color.White
        )
    }
}

@Composable
fun ExportButton(onClick: () -> Unit = {}) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Filled.SdCard,
            contentDescription = stringResource(R.string.action_export),
            tint = Color.White
        )
    }
}

@Composable
fun SearchTextField(
    searchTerm: String,
    onSearchTermChanged: (String) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    TextField(
        value = searchTerm,
        onValueChange = { newSearchTerm -> onSearchTermChanged(newSearchTerm) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = stringResource(R.string.action_search_notes),
                tint = Color.White
            )
        },
        label = { Text(stringResource(R.string.action_search_notes)) },
        singleLine = true,
        maxLines = 1,
        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White,
            textColor = Color.White,
            placeholderColor = Color.White,
            cursorColor = Color.White,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.White
        )
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun SearchNotesButton(onClick: () -> Unit = {}) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = stringResource(R.string.action_search_notes),
            tint = Color.White
        )
    }
}
