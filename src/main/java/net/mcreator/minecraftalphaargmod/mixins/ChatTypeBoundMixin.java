package net.mcreator.minecraftalphaargmod.mixins;

import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.client.GuiMessageTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public class ChatTypeBoundMixin {

    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;)V", 
            at = @At("HEAD"), cancellable = true)
    private void onAddSystemMessage(Component message, CallbackInfo ci) {
        String messageText = message.getString();
        
        if (messageText.startsWith("[SYSTEM] ")) {
            String cleanMessage = messageText.substring(9);
            Component newMessage = Component.literal(cleanMessage);
            
            ((ChatComponent)(Object)this).addMessage(newMessage, (MessageSignature)null, (GuiMessageTag)null);
            ci.cancel();
            return;
        }
        
       
        ((ChatComponent)(Object)this).addMessage(message, (MessageSignature)null, (GuiMessageTag)null);
        ci.cancel();
    }
}