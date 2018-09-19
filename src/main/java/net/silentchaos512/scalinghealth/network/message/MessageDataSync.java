package net.silentchaos512.scalinghealth.network.message;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.event.ClientTicks;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.config.Config;
import net.silentchaos512.scalinghealth.network.Message;
import net.silentchaos512.scalinghealth.utils.ModifierHandler;
import net.silentchaos512.scalinghealth.utils.SHPlayerDataHandler;
import net.silentchaos512.scalinghealth.utils.SHPlayerDataHandler.PlayerData;

@SuppressWarnings("WeakerAccess")
public class MessageDataSync extends Message {
    public NBTTagCompound tags;
    public String playerName;
    public int experienceLevel;

    @SuppressWarnings("unused")
    public MessageDataSync() {
    }

    public MessageDataSync(PlayerData data, EntityPlayer player) {

        tags = new NBTTagCompound();
        data.writeToNBT(tags);
        this.playerName = player.getName();
        this.experienceLevel = player.experienceLevel;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage handleMessage(MessageContext context) {

        ClientTicks.scheduleAction(() -> {
            EntityPlayer player = ScalingHealth.proxy.getClientPlayer().world.getPlayerEntityByName(playerName);
            if (player != null) {
                PlayerData data = SHPlayerDataHandler.get(player);
                if (data != null) {
                    data.readFromNBT(tags);

                    // Set players health and max health.
                    if (Config.Player.Health.allowModify) {
                        ModifierHandler.setMaxHealth(player, data.getMaxHealth(), 0);
                        if (data.getHealth() > 0f)
                            player.setHealth(data.getHealth());
                    }
                }

                player.experienceLevel = experienceLevel;
            }
        });

        return null;
    }
}
