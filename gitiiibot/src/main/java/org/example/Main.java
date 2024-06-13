package org.example;

import org.example.bot.Bot;
import org.example.botconfig.BotConfig;
import org.example.git.GitHubWatcher;
import org.example.observer.RepoObserver;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            String botUsername = BotConfig.BOT_NAME;
            String botToken = BotConfig.BOT_TOKEN;
            String githubToken = BotConfig.GITHUB_TOKEN;
            String chatId = "1055718708";

            Bot bot = new Bot(botUsername, botToken);
            botsApi.registerBot(bot);


            GitHubWatcher gitHubWatcher1 = new GitHubWatcher("https://api.github.com/repos/Temiann/testRept", githubToken);
            GitHubWatcher gitHubWatcher2 = new GitHubWatcher("https://api.github.com/repos/Temiann/testRept2", githubToken);
            GitHubWatcher gitHubWatcher3 = new GitHubWatcher("https://api.github.com/repos/ZXCpikachu/Test1234", githubToken);
            GitHubWatcher gitHubWatcher4 = new GitHubWatcher("https://api.github.com/repos/zapintix/BotBot", githubToken);

            RepoObserver observer = new RepoObserver(bot, chatId);
            gitHubWatcher1.addObserver(observer);
            gitHubWatcher2.addObserver(observer);
            gitHubWatcher3.addObserver(observer);
            gitHubWatcher4.addObserver(observer);

            Thread thread1 = new Thread(gitHubWatcher1);
            Thread thread2 = new Thread(gitHubWatcher2);
            Thread thread3 = new Thread(gitHubWatcher3);
            Thread thread4 = new Thread(gitHubWatcher4);

            thread1.start();
            thread2.start();
            thread3.start();
            thread4.start();

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}