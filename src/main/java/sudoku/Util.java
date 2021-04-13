package sudoku;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class Util {

    public static <T> Iterable<T> filter(Iterable<? extends T> iterable,
            Predicate<? super T> predicate) {
        return () -> new Iterator<T>() {

            Optional<T> nextOpt = Optional.empty();
            Iterator<? extends T> it = iterable.iterator();

            @Override
            public boolean hasNext() {
                if (nextOpt.isPresent())
                    return true;
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

    /**
     * Returns a deep copy of a map whose values are set of objets of an enumeration class.
     */
    public static <K, E extends Enum<E>> Map<K, Set<E>> copy(
            Map<? extends K, ? extends Set<E>> map) {
        Map<K, Set<E>> copy = new HashMap<>();
        for (K key : map.keySet()) {
            copy.put(key, EnumSet.copyOf(map.get(key)));
        }
        return copy;
    }

}
