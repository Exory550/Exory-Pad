package com.exory550.exorypad.usecase

import android.net.Uri
import com.exory550.exorypad.model.ExportedNotesDirectory
import com.exory550.exorypad.model.FilenameFormat
import com.exory550.exorypad.model.FilenameFormat.TimestampAndTitle
import com.exory550.exorypad.model.FilenameFormat.TitleAndTimestamp
import com.exory550.exorypad.model.FilenameFormat.TitleOnly
import com.exory550.exorypad.model.Note
import com.exory550.exorypad.model.NoteMetadata
import com.github.k1rakishou.fsaf.FileChooser
import com.github.k1rakishou.fsaf.FileManager
import com.github.k1rakishou.fsaf.callback.FileChooserCallback
import com.github.k1rakishou.fsaf.callback.FileCreateCallback
import com.github.k1rakishou.fsaf.callback.FileMultiSelectChooserCallback
import com.github.k1rakishou.fsaf.callback.directory.DirectoryChooserCallback
import com.github.k1rakishou.fsaf.file.FileSegment
import org.koin.core.module.dsl.new
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import org.koin.dsl.module

interface ArtVandelay {
    fun importNotes(
        onNotesSelected: (Int) -> Unit,
        saveImportedNote: (InputStream, String) -> Unit,
    )

    fun exportNotes(
        hydratedNotes: List<Note>,
        filenameFormat: FilenameFormat,
        onCancel: () -> Unit,
        saveExportedNote: (OutputStream, String) -> Unit,
    )

    fun importAllNotes(
        saveImportedNotes: (InputStream) -> Unit,
    )

    fun exportAllNotes(
        saveExportedNotes: (OutputStream) -> Unit,
    )

    fun exportSingleNote(
        metadata: NoteMetadata,
        filenameFormat: FilenameFormat,
        saveExportedNote: (OutputStream) -> Unit,
    )
}

private class ArtVandelayImpl(
    private val fileChooser: FileChooser,
    private val fileManager: FileManager
): ArtVandelay {
    override fun importNotes(
        onNotesSelected: (Int) -> Unit,
        saveImportedNote: (InputStream, String) -> Unit,
    ) = fileChooser.openChooseMultiSelectFileDialog(
        importCallback(onNotesSelected, saveImportedNote)
    )

    override fun importAllNotes(
        saveImportedNotes: (InputStream) -> Unit,
    ) = fileChooser.openChooseFileDialog(
        importAllCallback(saveImportedNotes)
    )

    override fun exportAllNotes(
        saveExportedNotes: (OutputStream) -> Unit,
    ) = fileChooser.openCreateFileDialog(
        fileName = generateExportFilename(),
        fileCreateCallback = exportAllCallback(saveExportedNotes)
    )

    override fun exportNotes(
        hydratedNotes: List<Note>,
        filenameFormat: FilenameFormat,
        onCancel: () -> Unit,
        saveExportedNote: (OutputStream, String) -> Unit,
    ) = fileChooser.openChooseDirectoryDialog(
        directoryChooserCallback = exportFolderCallback(
            hydratedNotes,
            filenameFormat,
            onCancel,
            saveExportedNote,
        )
    )

    override fun exportSingleNote(
        metadata: NoteMetadata,
        filenameFormat: FilenameFormat,
        saveExportedNote: (OutputStream) -> Unit,
    ) = fileChooser.openCreateFileDialog(
        fileName = generateFilename(metadata, filenameFormat),
        fileCreateCallback = exportFileCallback(saveExportedNote)
    )

    private val registeredBaseDirs = mutableListOf<Uri>()

    private fun importCallback(
        onNotesSelected: (Int) -> Unit,
        saveImportedNote: (InputStream, String) -> Unit,
    ) = object : FileMultiSelectChooserCallback() {
        override fun onResult(uris: List<Uri>) {
            onNotesSelected(uris.size)
            with(fileManager) {
                for (uri in uris) {
                    fromUri(uri)?.let { file ->
                        val filename = file.getFullPath()

                        getInputStream(file)?.let { input ->
                            saveImportedNote(input, filename)
                        }
                    }
                }
            }
        }

        override fun onCancel(reason: String) = Unit
    }

    private fun importAllCallback(
        saveImportedNotes: (InputStream) -> Unit,
    ) = object : FileChooserCallback() {
        override fun onResult(uri: Uri) {
            with(fileManager) {
                fromUri(uri)?.let(::getInputStream)?.let { input ->
                    saveImportedNotes(input)
                }
            }
        }

        override fun onCancel(reason: String) = Unit
    }

    private fun exportAllCallback(
        saveExportedNotes: (OutputStream) -> Unit,
    ) = object : FileCreateCallback() {
        override fun onResult(uri: Uri) {
            with(fileManager) {
                fromUri(uri)?.let(::getOutputStream)?.let { output ->
                    saveExportedNotes(output)
                }
            }
        }

        override fun onCancel(reason: String) = Unit
    }


    private fun exportFolderCallback(
        hydratedNotes: List<Note>,
        filenameFormat: FilenameFormat,
        onCancel: () -> Unit,
        saveExportedNote: (OutputStream, String) -> Unit,
    ) = object: DirectoryChooserCallback() {
        override fun onResult(uri: Uri) {
            with(fileManager) {
                if (!registeredBaseDirs.contains(uri)) {
                    registerBaseDir<ExportedNotesDirectory>(ExportedNotesDirectory(uri))
                    registeredBaseDirs.add(uri)
                }

                newBaseDirectoryFile<ExportedNotesDirectory>()?.let { baseDir ->
                    for (note in hydratedNotes) {
                        val filename = generateFilename(note.metadata, filenameFormat)
                        create(baseDir, FileSegment(filename))
                            ?.let(::getOutputStream)
                            ?.let { output ->
                                saveExportedNote(output, note.text)
                            }
                    }
                }
            }
        }

        override fun onCancel(reason: String) = onCancel()
    }

    private fun exportFileCallback(
        saveExportedNote: (OutputStream) -> Unit,
    ) = object: FileCreateCallback() {
        override fun onResult(uri: Uri) {
            with(fileManager) {
                fromUri(uri)?.let(::getOutputStream)?.let(saveExportedNote)
            }
        }

        override fun onCancel(reason: String) = Unit
    }

    private fun generateExportFilename(): String {
        val title = "exorypad_backup"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm", Locale.getDefault())
        val timestamp = dateFormat.format(Date())
        val filename = "${title}_$timestamp"

        return "$filename.json"
    }

    private fun generateFilename(
        metadata: NoteMetadata,
        filenameFormat: FilenameFormat
    ): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm", Locale.getDefault())
        val timestamp = dateFormat.format(metadata.date)
        val filename = when(filenameFormat) {
            TitleOnly -> metadata.title.take(245)
            TimestampAndTitle -> "${timestamp}_${metadata.title.take(245 - (timestamp.length + 1))}"
            TitleAndTimestamp -> "${metadata.title.take(245 - (timestamp.length + 1))}_$timestamp"
        }

        return "$filename.txt"
    }
}

val artVandelayModule = module {
    single<ArtVandelay> { new(::ArtVandelayImpl) }
}
