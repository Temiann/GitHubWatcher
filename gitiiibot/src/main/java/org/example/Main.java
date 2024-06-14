package org.example;

import org.example.bot.Bot;
import org.example.botconfig.BotConfig;
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
            String chatId = "1055718708";

            RepoObserver observer = new RepoObserver(null, chatId);

            Bot bot = new Bot(botUsername, botToken, observer);

            observer.setBot(bot);

            botsApi.registerBot(bot);

            bot.addRepo("https://api.github.com/repos/Temiann/testRept", Long.parseLong(chatId));
            bot.addRepo("https://api.github.com/repos/Temiann/testRept2", Long.parseLong(chatId));
            bot.addRepo("https://api.github.com/repos/ZXCpikachu/Test1234", Long.parseLong(chatId));
            bot.addRepo("https://api.github.com/repos/zapintix/BotBot", Long.parseLong(chatId));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}