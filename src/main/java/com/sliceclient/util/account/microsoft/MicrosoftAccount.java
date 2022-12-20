package com.sliceclient.util.account.microsoft;

import fr.litarvan.openauth.microsoft.model.response.MinecraftProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.util.Session;

@Getter @Setter
@AllArgsConstructor
public class MicrosoftAccount {
    private MinecraftProfile profile;
    private Session session;
    private String refreshToken;
}
