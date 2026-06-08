package net.mcreator.minecraftalphaargmod.client;  
  
import java.util.Set;  
  
public class ConfigRestartPolicy {  
  
    private static final Set<String> REQUIRES_RESTART = Set.of(  
        "quickload_button",  
        "dont_look_at_the_moon"  
        
    );  
  
    public static boolean requiresRestart(String key) {  
        return REQUIRES_RESTART.contains(key);  
    }  
}