package quickbeer.android.next.rx;

import android.support.annotation.NonNull;

import polanski.option.Option;
import polanski.option.OptionUnsafe;
import quickbeer.android.next.pojo.ItemList;
import rx.Observable;
import rx.functions.Func1;

public final class RxUtils<T> {

    @NonNull
    public static <T> Observable<T> pickValue(@NonNull final Observable<Option<T>> observable) {
        return observable.filter(Option::isSome)
                         .map(OptionUnsafe::getUnsafe);
    }

    @NonNull
    public static <T> Boolean isNoneOrEmpty(@NonNull final Option<ItemList<T>> option) {
        return option.match(list -> list.getItems().isEmpty(), () -> true);
    }

}
