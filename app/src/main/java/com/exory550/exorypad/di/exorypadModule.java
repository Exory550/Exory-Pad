package com.exory550.exorypad.di;

import android.content.Context;
import com.exory550.exorypad.Database;
import com.exory550.exorypad.data.ExorypadRepository;
import com.exory550.exorypad.model.NoteMetadata;
import com.exory550.exorypad.usecase.artVandelayModule;
import com.exory550.exorypad.usecase.dataMigratorModule;
import com.exory550.exorypad.usecase.keyboardShortcutsModule;
import com.exory550.exorypad.usecase.systemThemeModule;
import com.exory550.exorypad.usecase.toasterModule;
import com.exory550.exorypad.utils.dataStore;
import com.exory550.exorypad.viewmodel.viewModelModule;
import com.github.k1rakishou.fsaf.FileChooser;
import com.github.k1rakishou.fsaf.FileManager;
import com.squareup.sqldelight.ColumnAdapter;
import com.squareup.sqldelight.android.AndroidSqliteDriver;
import de.schnettler.datastore.manager.DataStoreManager;
import java.util.Date;
import org.koin.android.ext.koin.androidContext;
import org.koin.dsl.module;

public class exorypadModule {
    public static final org.koin.core.module.Module module = module {
        includes(
            viewModelModule,
            dataMigratorModule,
            toasterModule,
            artVandelayModule,
            keyboardShortcutsModule,
            systemThemeModule
        );

        single { provideDatabase(context = androidContext()) };
        single { new ExorypadRepository(database = get()) };
        single { new DataStoreManager(dataStore = androidContext().dataStore) };
        single { new FileManager(appContext = androidContext()) };
        single { new FileChooser(appContext = androidContext()) };
    };

    private static Database provideDatabase(Context context) {
        return new Database(
            driver = new AndroidSqliteDriver(Database.Schema, context, "exorypad.db"),
            NoteMetadataAdapter = new NoteMetadata.Adapter(dateAdapter = DateAdapter.INSTANCE)
        );
    }

    public static final class DateAdapter implements ColumnAdapter<Date, Long> {
        public static final DateAdapter INSTANCE = new DateAdapter();

        private DateAdapter() {}

        @Override
        public Date decode(Long databaseValue) {
            return new Date(databaseValue);
        }

        @Override
        public Long encode(Date value) {
            return value.getTime();
        }
    }
}
