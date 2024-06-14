package org.example.bot;

import org.example.botconfig.BotConfig;
import org.example.git.GitHubWatcher;
import org.example.observer.RepoObserver;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

public class Bot extends TelegramLongPollingBot {
    private final Map<String, GitHubWatcher> watchers = new HashMap<>();
    private final RepoObserver observer;

    public Bot(String botUsername, String botToken, RepoObserver observer) {
        super();
        this.observer = observer;
    }

    @Override
    public String getBotUsername() {
        return BotConfig.BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BotConfig.BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String username = update.getMessage().getChat().getFirstName();

            if (messageText.equals("/start")) {
                String answer = "Привет! " + username;
                sendMessage(chatId, answer);
            } else if (messageText.startsWith("/addrepo ")) {
                String repoUrl = messageText.split(" ")[1];
                addRepo(repoUrl, chatId);
            } else if (messageText.startsWith("/settime ")){
                String time  = messageText.split(" ")[1];
                settime(time, chatId);
            }
        }
    }

    public void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.enableMarkdown(true);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void addRepo(String repoUrl, long chatId) {
        if (!watchers.containsKey(repoUrl)) {
            GitHubWatcher watcher = new GitHubWatcher(repoUrl, BotConfig.GITHUB_TOKEN);
            watcher.addObserver(observer);
            Thread thread = new Thread(watcher);
            thread.start();
            watchers.put(repoUrl, watcher);
            sendMessage(chatId, "Репозиторий " + repoUrl + " добавлен для отслеживания.");
        } else {
            sendMessage(chatId, "Репозиторий " + repoUrl + " уже отслеживается.");
        }
    }
//    public void settime(String time, long chatId){
//        if(time != null){
//            GitHubWatcher watcher = new GitHubWatcher();
//            int timer = Integer.parseInt (time);
//            watcher.setTime(timer);
//            System.out.println("Установлено время " + timer);
//            sendMessage(chatId, "Успешно установлено время");
//        } else {
//            sendMessage(chatId, "Не удалось установить время обновления");
//        }
//    }
    public void settime(String time, long chatId) {
        if (time != null) {
            try {
                int timer = Integer.parseInt(time);
                for (GitHubWatcher watcher : watchers.values()) {
                    watcher.setTime(timer);
                }
                System.out.println("Установлено время " + timer);
                sendMessage(chatId, "Успешно установлено время: " + timer);
            } catch (NumberFormatException e) {
                sendMessage(chatId, "Время должно быть числом.");
            }
        } else {
            sendMessage(chatId, "Не удалось установить время обновления.");
        }
    }
}