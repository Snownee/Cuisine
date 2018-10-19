package snownee.cuisine.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

/**
 * A simple wrapper of a String to object map, functioning as a namespace-based
 * registry. It runs on a "first come, first serve" basis - the first one that
 * register a thing with a certain identifier will permanently acquire it,
 * until the registry is invalidated.
 * 
 * @param <E>
 *            The type of actual object to be registered
 */
final class IdentifierBasedRegistry<E>
{
    private final Map<String, E> registry;

    IdentifierBasedRegistry()
    {
        this(new HashMap<>(16));
    }

    IdentifierBasedRegistry(Map<String, E> underlyingMap)
    {
        this.registry = underlyingMap;
    }

    public E register(String identifier, E candidate)
    {
        E ret = registry.putIfAbsent(Objects.requireNonNull(identifier), candidate);
        return ret == null ? candidate : ret;
    }

    @Nullable
    public E lookup(String identifier)
    {
        return registry.get(identifier);
    }

    public Map<String, E> getView()
    {
        return Collections.unmodifiableMap(registry);
    }
}
