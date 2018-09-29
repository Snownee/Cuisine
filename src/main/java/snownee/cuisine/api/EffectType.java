package snownee.cuisine.api;

public interface EffectType<T>
{
    Class<? extends T> getEffectClass();
}
