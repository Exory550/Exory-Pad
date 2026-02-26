package com.exory550.exorypad.data;

import androidx.compose.ui.text.font.FontFamily;
import androidx.compose.ui.unit.sp;
import com.exory550.exorypad.R;
import com.exory550.exorypad.model.FilenameFormat;
import com.exory550.exorypad.model.Prefs;
import com.exory550.exorypad.model.SortOrder;
import com.exory550.exorypad.usecase.SystemTheme;
import de.schnettler.datastore.manager.DataStoreManager;
import de.schnettler.datastore.manager.PreferenceRequest;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.SharingStarted;
import kotlinx.coroutines.flow.map;
import kotlinx.coroutines.flow.stateIn;

public class PreferenceManager {
    private final DataStoreManager dataStoreManager;
    private final CoroutineScope scope;
    private final SystemTheme systemTheme;

    private PreferenceManager(DataStoreManager dataStoreManager, CoroutineScope scope, SystemTheme systemTheme) {
        this.dataStoreManager = dataStoreManager;
        this.scope = scope;
        this.systemTheme = systemTheme;
    }

    public Flow<Boolean> getIsLightTheme() {
        return mapToFlow(Prefs.ColorScheme, theme -> theme.equals("light"));
    }

    public Flow<Integer> getBackgroundColorRes() {
        return mapToFlow(Prefs.ColorScheme, theme -> {
            if (theme.equals("light")) {
                return R.color.window_background;
            } else {
                return R.color.window_background_dark;
            }
        });
    }

    public Flow<Integer> getPrimaryColorRes() {
        return mapToFlow(Prefs.ColorScheme, theme -> {
            if (theme.equals("light")) {
                return R.color.text_color_primary;
            } else {
                return R.color.text_color_primary_dark;
            }
        });
    }

    public Flow<Integer> getSecondaryColorRes() {
        return mapToFlow(Prefs.ColorScheme, theme -> {
            if (theme.equals("light")) {
                return R.color.text_color_secondary;
            } else {
                return R.color.text_color_secondary_dark;
            }
        });
    }

    public Flow<TextUnit> getTextFontSize() {
        return mapToFlow(Prefs.FontSize, fontSize -> {
            switch(fontSize) {
                case "smallest": return 12.sp;
                case "small": return 14.sp;
                case "normal": return 16.sp;
                case "large": return 18.sp;
                default: return 20.sp;
            }
        });
    }

    public Flow<TextUnit> getDateFontSize() {
        return mapToFlow(Prefs.FontSize, fontSize -> {
            switch(fontSize) {
                case "smallest": return 8.sp;
                case "small": return 10.sp;
                case "normal": return 12.sp;
                case "large": return 14.sp;
                default: return 16.sp;
            }
        });
    }

    public Flow<FontFamily> getFontFamily() {
        return mapToFlow(Prefs.FontType, theme -> {
            switch(theme) {
                case "sans": return FontFamily.SansSerif;
                case "serif": return FontFamily.Serif;
                default: return FontFamily.Monospace;
            }
        });
    }

    public Flow<SortOrder> getSortOrder() {
        return mapToFlow(Prefs.SortBy, value -> toSortOrder(value));
    }

    public Flow<FilenameFormat> getFilenameFormat() {
        return mapToFlow(Prefs.ExportFilename, value -> toFilenameFormat(value));
    }

    public Flow<Boolean> getShowDialogs() {
        return asFlow(Prefs.ShowDialogs);
    }

    public Flow<Boolean> getShowDate() {
        return asFlow(Prefs.ShowDate);
    }

    public Flow<Boolean> getDirectEdit() {
        return asFlow(Prefs.DirectEdit);
    }

    public Flow<Boolean> getMarkdown() {
        return asFlow(Prefs.Markdown);
    }

    public Flow<Boolean> getRtlLayout() {
        return asFlow(Prefs.RtlSupport);
    }

    public Flow<Boolean> getFirstRunComplete() {
        return mapToFlow(Prefs.FirstRun, value -> toBoolean(value));
    }

    public Flow<Boolean> getFirstViewComplete() {
        return mapToFlow(Prefs.FirstLoad, value -> toBoolean(value));
    }

    public Flow<Boolean> getShowDoubleTapMessage() {
        return asFlow(Prefs.ShowDoubleTapMessage);
    }

    @SuppressWarnings("unchecked")
    private <T, R> Flow<R> mapToFlow(PreferenceRequest<T> request, Function<T, R> transform) {
        return dataStoreManager.getPreferenceFlow(request)
            .map(value -> {
                if (request instanceof Prefs.ColorScheme && value instanceof String && value.equals("system")) {
                    return (T) systemTheme.getColorScheme().getStringValue();
                }
                return value;
            })
            .map(transform)
            .stateIn(scope, SharingStarted.Companion.getLazily(), transform.apply(request.getDefaultValue()));
    }

    private <T> Flow<T> asFlow(PreferenceRequest<T> request) {
        return mapToFlow(request, value -> value);
    }

    private SortOrder toSortOrder(String value) {
        for (SortOrder order : SortOrder.entries) {
            if (order.getStringValue().equals(value)) {
                return order;
            }
        }
        return SortOrder.DateDescending;
    }

    private FilenameFormat toFilenameFormat(String value) {
        for (FilenameFormat format : FilenameFormat.entries) {
            if (format.getStringValue().equals(value)) {
                return format;
            }
        }
        return FilenameFormat.Timestamp;
    }

    private boolean toBoolean(int value) {
        return value > 0;
    }

    public static PreferenceManager prefs(DataStoreManager dataStoreManager, CoroutineScope scope, SystemTheme systemTheme) {
        return new PreferenceManager(dataStoreManager, scope, systemTheme);
    }
}
