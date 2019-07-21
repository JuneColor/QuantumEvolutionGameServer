package evolution.game.impl;

import evolution.game.Game;

import java.net.Socket;
import java.util.List;

public class DemoGame implements Game {
    @Override
    public boolean createGame(String gameId, String gameName) {
        return false;
    }

    @Override
    public boolean addPlayer(String playerId, Socket socket) {
        return false;
    }

    @Override
    public List<String> listAllPlayerId() {
        return null;
    }

    @Override
    public boolean removePlayer(String playerId) {
        return false;
    }

    @Override
    public boolean isGameReady() {
        return false;
    }

    @Override
    public boolean startGame() {
        return false;
    }

    @Override
    public boolean destroyGame() {
        return false;
    }
}
