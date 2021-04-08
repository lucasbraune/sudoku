package util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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

    private static <T> List<Iterator<? extends T>> getIterators(List<Iterable<? extends T>> iterables) {
        List<Iterator<? extends T>> iterators = new ArrayList<>();
        for (Iterable<? extends T> iterable : iterables) {
            iterators.add(iterable.iterator());
        }
        return iterators;
    }

    public static <T> Iterable<T> concatenate(List<Iterable<? extends T>> iterables) {
        return () -> new Iterator<T>() {
            List<Iterator<? extends T>> iterators = getIterators(iterables);
            Optional<T> nextOpt = Optional.empty();

            @Override
            public boolean hasNext() {
                if (nextOpt.isPresent()) {
                    return true;
                }
                for (Iterator<? extends T> it : iterators) {
                    if (it.hasNext()) {
                        nextOpt = Optional.of(it.next());
                        return true;
                    }
                }
                return false;
            }

            @Override
            public T next() {
                T nextVal = nextOpt.get();
                nextOpt = Optional.empty();
                return nextVal;
            }

        };
    }

    /**
     * Returns a deep copy of a map whose values are set of objets of an enumeration
     * class.
     */
    public static <K, E extends Enum<E>> Map<K, Set<E>> copy(Map<? extends K, ? extends Set<E>> map) {
        Map<K, Set<E>> copy = new HashMap<>();
        for (K key : map.keySet()) {
            copy.put(key, EnumSet.copyOf(map.get(key)));
        }
        return copy;
    }
    
}
