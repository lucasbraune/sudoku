package util;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Util {

    public static <T> Stream<T> makeStream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), /* parallel? */ false);
    }

    public static <T> Iterable<T> filter(Iterable<? extends T> iterable, Predicate<? super T> predicate) {
        return () -> new Iterator<T>() {

            Optional<T> nextOpt = Optional.empty();
            Iterator<? extends T> it = iterable.iterator();

            @Override
            public boolean hasNext() {
                if (nextOpt.isPresent()) return true;
                while (it.hasNext()) {
                    T candidate = it.next();
                    if (predicate.test(candidate)) {
                        nextOpt = Optional.of(candidate);
                        return true;
                    }
                }
                return false;
            }

            @Override
            public T next() {
                T nextValue = nextOpt.get();
                nextOpt = Optional.empty();
                return nextValue;
            }

        };
    }
    
}
