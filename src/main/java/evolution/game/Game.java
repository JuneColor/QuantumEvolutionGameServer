package evolution.game;

import com.google.inject.ImplementedBy;
import evolution.game.impl.DemoGame;

import java.net.Socket;
import java.util.List;

@ImplementedBy(DemoGame.class)
public interface Game {
    boolean createGame(String gameId, String gameName);
    boolean addPlayer(String playerId, Socket socket);
    List<String> listAllPlayerId();
    boolean removePlayer(String playerId);
    boolean isGameReady();
    boolean startGame();
    boolean destroyGame();
}
