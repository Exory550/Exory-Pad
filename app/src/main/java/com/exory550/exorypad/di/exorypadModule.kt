package com.exory550.exorypad.di

import android.content.Context
import com.exory550.exorypad.Database
import com.exory550.exorypad.data.ExorypadRepository
import com.exory550.exorypad.model.NoteMetadata
import com.exory550.exorypad.usecase.artVandelayModule
import com.exory550.exorypad.usecase.dataMigratorModule
import com.exory550.exorypad.usecase.keyboardShortcutsModule
import com.exory550.exorypad.usecase.systemThemeModule
import com.exory550.exorypad.usecase.toasterModule
import com.exory550.exorypad.utils.dataStore
import com.exory550.exorypad.viewmodel.viewModelModule
import com.github.k1rakishou.fsaf.FileChooser
import com.github.k1rakishou.fsaf.FileManager
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import de.schnettler.datastore.manager.DataStoreManager
import java.util.Date
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val exorypadModule = module {
    includes(
        viewModelModule,
        dataMigratorModule,
        toasterModule,
        artVandelayModule,
        keyboardShortcutsModule,
        systemThemeModule
    )

    single { provideDatabase(context = androidContext()) }
    single { ExorypadRepository(database = get()) }
    single { DataStoreManager(dataStore = androidContext().dataStore) }
    single { FileManager(appContext = androidContext()) }
    single { FileChooser(appContext = androidContext()) }
}

private fun provideDatabase(context: Context) = Database(
    driver = AndroidSqliteDriver(Database.Schema, context, "exorypad.db"),
    NoteMetadataAdapter = NoteMetadata.Adapter(dateAdapter = DateAdapter)
)

object DateAdapter: ColumnAdapter<Date, Long> {
    override fun decode(databaseValue: Long) = Date(databaseValue)
    override fun encode(value: Date) = value.time
}
