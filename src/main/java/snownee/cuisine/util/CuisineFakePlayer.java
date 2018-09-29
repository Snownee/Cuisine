package snownee.cuisine.util;

import java.lang.ref.WeakReference;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.mojang.authlib.GameProfile;

import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

public final class CuisineFakePlayer
{

    // TODO (3TUSK): A fixed UUID for better control and compatibility (e.g. Sponge's GameProfile lookup blacklist thing)
    private static final GameProfile CUISINE_FAKE_PLAYER_PROFILE = new GameProfile(UUID.randomUUID(), "[Cuisine]");

    private static WeakReference<FakePlayer> fakePlayerHolder = null;

    @Nonnull
    public static FakePlayer instance(WorldServer world)
    {
        FakePlayer instance;
        if (fakePlayerHolder == null || (instance = fakePlayerHolder.get()) == null)
        {
            fakePlayerHolder = new WeakReference<>((instance = FakePlayerFactory.get(world, CUISINE_FAKE_PLAYER_PROFILE)));
        }

        return instance;
    }
}
