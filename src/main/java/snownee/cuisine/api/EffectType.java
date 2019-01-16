package snownee.cuisine.api;

public interface EffectType<T>
{
    Class<? extends T> getEffectClass();

    default <R> R cast(T value)
    {
        return (R) value;
    }
}
