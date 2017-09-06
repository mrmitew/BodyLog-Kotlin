package com.github.mrmitew.bodylog.adapter.profile_details.last_updated.model;

import com.github.mrmitew.bodylog.adapter.common.model.StateError;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class LastUpdatedTextState {
    public static final String EMPTY = "n/a";

    public static class Factory {
        public static LastUpdatedTextState idle() {
            return builder().
                    lastUpdated(EMPTY)
                    .error(StateError.Empty.INSTANCE)
                    .build();
        }

        public static LastUpdatedTextState success(long time) {
            return builder().
                    // TODO: Use a date formatter
                            lastUpdated(String.valueOf(time))
                    .error(StateError.Empty.INSTANCE)
                    .build();
        }

        public static LastUpdatedTextState error(Throwable error) {
            return builder().
                    lastUpdated(EMPTY)
                    .error(error)
                    .build();
        }
    }

    public abstract String lastUpdated();

    public abstract Throwable error();

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_LastUpdatedTextState.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder lastUpdated(final String lastUpdated);

        public abstract Builder error(final Throwable error);

        public abstract LastUpdatedTextState build();
    }
}
