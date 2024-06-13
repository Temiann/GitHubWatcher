package org.example.observer;

import org.example.bot.Bot;
import org.example.git.GitHubWatcher;

import java.util.Observable;
import java.util.Observer;

public class RepoObserver implements Observer {
    private Bot bot;
    private String chatId;

    public RepoObserver(Bot bot, String chatId) {
        this.bot = bot;
        this.chatId = chatId;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof GitHubWatcher) {
            String message = (String) arg;
            System.out.println("Notifying bot with message: " + message);
            bot.sendMessage(Long.parseLong(chatId), message);
        }
    }
}