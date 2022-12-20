package com.sliceclient.util.account;

import com.google.common.collect.ImmutableMap;
import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.thealtening.AlteningServiceType;
import com.thealtening.SSLController;
import com.thealtening.TheAlteningAuthentication;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import fr.litarvan.openauth.microsoft.model.response.MinecraftProfile;
import lombok.experimental.UtilityClass;
import com.sliceclient.util.account.microsoft.MicrosoftAccount;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;

import java.lang.reflect.Field;
import java.net.Proxy;
import java.util.Optional;

/**
 * Utility class to log in to accounts
 *
 * @author Nick
 * */
@UtilityClass
public class LoginUtil {

    private static final SSLController ssl = new SSLController();
    private static final TheAlteningAuthentication serviceSwitch = TheAlteningAuthentication.mojang();

    /**
     * Logins in using email and password.
     * */
    public static Session loginMojang(String email, String password) {
        final YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        final YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) service.createUserAuthentication(Agent.MINECRAFT);

        serviceSwitch.updateService(AlteningServiceType.MOJANG);

        auth.setUsername(email);
        auth.setPassword(password);

        try {
            auth.logIn();
            return new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), Optional.of(""), Optional.of(""), Session.AccountType.MOJANG);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Logins in using an altening token.
     * */
    public static Session loginAlteining(String token) {
        YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) service.createUserAuthentication(Agent.MINECRAFT);

        ssl.disableCertificateValidation();
        serviceSwitch.updateService(AlteningServiceType.THEALTENING);

        auth.setUsername(token);
        auth.setPassword(token);

        try {
            auth.logIn();
        } catch (Exception ignored){}

        return new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), Optional.of(""),Optional.of(""), Session.AccountType.MOJANG);
    }

    /**
     * Logins user in using Microsoft Authenticator.
     */
    public static MicrosoftAccount loginMicrosoft(String email, String password) {
        try {
            serviceSwitch.updateService(AlteningServiceType.MOJANG);

            MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
            MicrosoftAuthResult result = authenticator.loginWithCredentials(email, password);
            MinecraftProfile profile = result.getProfile();

            Session session = new Session(profile.getName(), profile.getId(), result.getAccessToken(), Optional.of(""),Optional.of(""), Session.AccountType.MSA);
            setSession(session);
            return new MicrosoftAccount(profile, session, result.getRefreshToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static MicrosoftAccount loginMicrosoftNoSetSession(String email, String password) {
        try {
          serviceSwitch.updateService(AlteningServiceType.MOJANG);
            MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
            MicrosoftAuthResult result = authenticator.loginWithCredentials(email, password);
            MinecraftProfile profile = result.getProfile();
            Session session = new Session(profile.getName(), profile.getId(), result.getAccessToken(), Optional.of(""),Optional.of(""), Session.AccountType.MSA);
            return new MicrosoftAccount(profile, session, result.getRefreshToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Logs user in as offline. (cracked account)
     */
    public static Session loginOffline(String username) {
        serviceSwitch.updateService(AlteningServiceType.MOJANG);
        return setSession(new Session(username, "0", "0", Optional.of(""),Optional.of(""), Session.AccountType.LEGACY));
    }

    private Session setSession(Session session) {
        MinecraftClient client = MinecraftClient.getInstance();
        try {
            Field field = client.getClass().getDeclaredField("session");
            field.setAccessible(true);
            field.set(client, session);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return MinecraftClient.getInstance().getSession();
    }
}
