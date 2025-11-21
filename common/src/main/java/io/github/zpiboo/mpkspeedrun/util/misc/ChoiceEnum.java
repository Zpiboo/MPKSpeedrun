package io.github.zpiboo.mpkspeedrun.util.misc;

import java.util.Arrays;

public interface ChoiceEnum<T extends Enum<T> & ChoiceEnum<T>> {
    @SuppressWarnings("unchecked")
    default T[] getValues() {
        return (T[]) this.getClass().getEnumConstants();
    }

    default T getPrevious() {
        T[] values = getValues();
        int i = Arrays.asList(values).indexOf(this);
        return values[(i - 1 + values.length) % values.length];
    }
    default T getNext() {
        T[] values = getValues();
        int i = Arrays.asList(values).indexOf(this);
        return values[(i + 1) % values.length];
    }
}
