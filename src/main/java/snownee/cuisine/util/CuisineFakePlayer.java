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
    /*
     * You may ask why the UUID looks strange. It's randomly generated in 44642 seconds,
     * using a Lua script (powered by https://github.com/Tieske/uuid). The script used
     * is provided below:
     *
     * local uuid = require "uuid"
     * print("Searching start at: " .. os.date())
     *
     * local start = os.time()
     *
     * while true do
     *   local u = uuid.new()
     *   if string.find(u, "94366666-", 1, true) then
     *     print(u)
     *     break
     *   end
     * end
     *
     * local endTime = os.time()
     *
     * print("Searching end at: " .. os.date())
     *
     * print("Total time cost: " .. (endTime - start))
     *
     * Yes it does not utilize coroutine, but at least it works.
     */
    private static final GameProfile CUISINE_FAKE_PLAYER_PROFILE = new GameProfile(UUID.fromString("94366666-e5fa-46b2-c69e-f6c9e659454e"), "[Cuisine]");

    // We use a WeakReference to make sure that the EntityPlayer reference is not leaked out
    private static WeakReference<FakePlayer> fakePlayerHolder = null;

    @Nonnull
    public static FakePlayer getInstance(WorldServer world)
    {
        FakePlayer instance;
        if (fakePlayerHolder == null || (instance = fakePlayerHolder.get()) == null)
        {
            fakePlayerHolder = new WeakReference<>((instance = FakePlayerFactory.get(world, CUISINE_FAKE_PLAYER_PROFILE)));
        }

        return instance;
    }
}
