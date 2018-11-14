package me.procedures.penguin.queue.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.procedures.penguin.PenguinPlugin;
import me.procedures.penguin.inventories.StateInventories;
import me.procedures.penguin.ladder.impl.Ladder;
import me.procedures.penguin.player.PlayerProfile;
import me.procedures.penguin.player.PlayerState;
import me.procedures.penguin.queue.impl.QueuePlayer;
import me.procedures.penguin.utils.GameUtil;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedList;
import java.util.List;

@Getter
public abstract class AbstractQueue{

    private final PenguinPlugin plugin;
    private final Ladder ladder;

    private List<QueuePlayer> queue = new LinkedList<>();

    private BukkitTask queueTask;

    private int playingAmount;

    public AbstractQueue(PenguinPlugin plugin, Ladder ladder) {
        this.plugin = plugin;
        this.ladder = ladder;
    }

    public abstract void createMatch(QueuePlayer playerOne, QueuePlayer playerTwo);

    public abstract void startQueueTask();

    public void addToQueue(Player player) {
        PlayerProfile playerProfile = this.plugin.getProfileManager().getProfile(player);
        QueuePlayer queuePlayer = new QueuePlayer(player);

        int min = playerProfile.getRatings().get(this.ladder).getRating() - 250;
        int max = playerProfile.getRatings().get(this.ladder).getRating() + 250;

        queuePlayer.setMin(min);
        queuePlayer.setMax(max);

        this.queue.add(queuePlayer);

        GameUtil.resetPlayer(player);
        player.getInventory().setContents(StateInventories.QUEUE.getContents());
        playerProfile.setState(PlayerState.QUEUING);

        this.playingAmount++;
    }

    public void removeFromQueue(Player player) {
        PlayerProfile playerProfile = this.plugin.getProfileManager().getProfile(player);

        GameUtil.resetPlayer(player);
        player.getInventory().setContents(StateInventories.LOBBY.getContents());
        playerProfile.setState(PlayerState.LOBBY);

        for (QueuePlayer queuePlayer : this.queue) {
            if (player == queuePlayer.getPlayer()) {
                this.queue.remove(queuePlayer);
                break;
            }
        }

        this.playingAmount--;
    }
}