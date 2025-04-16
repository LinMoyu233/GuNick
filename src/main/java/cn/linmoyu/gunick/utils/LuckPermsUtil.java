package cn.linmoyu.gunick.utils;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.ChatMetaNode;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class LuckPermsUtil {

    static LuckPerms luckPerms = LuckPermsProvider.get();
    static UserManager userManager = luckPerms.getUserManager();
    static GroupManager groupManager = luckPerms.getGroupManager();

    public static String getPrefix(UUID uuid) {
        User user = getUser(uuid);
        if (user == null) return "";
        String prefix = user.getCachedData().getMetaData().getPrefix();
        if (prefix == null) return "";
        return prefix;
    }

    public static String getSuffix(UUID uuid) {
        User user = getUser(uuid);
        if (user == null) return "";
        String suffix = user.getCachedData().getMetaData().getSuffix();
        if (suffix == null) return "";
        return suffix;
    }

    public static User getUser(UUID uuid) {
        if (userManager == null) {
            return null;
        }

        if (userManager.isLoaded(uuid)) {
            return userManager.getUser(uuid);
        } else {
            CompletableFuture<User> userCompletableFuture = userManager.loadUser(uuid);
            return userCompletableFuture.join();
        }
    }

    public static void setPrefix(UUID uuid, String prefix) {
        luckPerms.getUserManager().loadUser(uuid)
                .thenApplyAsync(user -> {
                    int priority = user.getNodes(NodeType.PREFIX).stream()
                            .mapToInt(ChatMetaNode::getPriority)
                            .max()
                            .orElse(0);
                    user.data().clear(NodeType.PREFIX::matches);
                    PrefixNode prefixNode = PrefixNode.builder(prefix, priority).build();
                    user.data().add(prefixNode);
                    luckPerms.getUserManager().saveUser(user);
                    return true;
                });
    }

    public static void setSuffix(UUID uuid, String suffix) {
        luckPerms.getUserManager().loadUser(uuid)
                .thenApplyAsync(user -> {
                    int priority = user.getNodes(NodeType.SUFFIX).stream()
                            .mapToInt(ChatMetaNode::getPriority)
                            .max()
                            .orElse(0);
                    user.data().clear(NodeType.SUFFIX::matches);
                    SuffixNode suffixNodeNode = SuffixNode.builder(suffix, priority).build();
                    user.data().add(suffixNodeNode);
                    luckPerms.getUserManager().saveUser(user);
                    return true;
                });
    }

//    public static String getPrefix(Player player) {
//        User user = getUser(player);
//        if (user == null) return "";
//        String prefix = user.getCachedData().getMetaData().getPrefix();
//        if (prefix == null) return "";
//
//        return Messages.translateCC(prefix);
//    }
//
//    public static String getSuffix(Player player) {
//        User user = getUser(player);
//        if (user == null) return "";
//        String suffix = user.getCachedData().getMetaData().getSuffix();
//        if (suffix == null) return "";
//
//        return Messages.translateCC(suffix);
//    }

//    private static User getUser(Player player) {
//        if (player == null) return null;
//
//        return luckPerms.getUserManager().getUser(player.getUniqueId());
//    }

}