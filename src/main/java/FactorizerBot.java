import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.io.InvalidObjectException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class FactorizerBot extends TelegramLongPollingBot {

    private String botToken;

    public FactorizerBot(String token) {
        this();
        this.botToken = token;
    }

    public FactorizerBot() {

    }

    @Override
    public String getBotToken() {
        if (this.botToken == null) {
            System.out.println("Please provide a bot token:");
            this.botToken = new Scanner(System.in).nextLine();
        }
        return this.botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            handleMessage(update);
        } catch (Exception e) {
        }
    }

    @Override
    public String getBotUsername() {
        return "Factorizer";
    }

    private void handleMessage(Update update) throws InvalidObjectException {

        Message message = update.getMessage();

        if (message.getText().equalsIgnoreCase("/start")) {
            sendMessage("Welcome!\nSend an integer to factorize it!", message);
        } else {
            String input = message.getText();
            try {
                BigInteger bi = new BigInteger(input);
                Map<BigInteger, BigInteger> out = powerifyBig(PollardRho.factor(bi));

                sendMessage(out.keySet().stream().sorted(BigInteger::compareTo).map(t -> out.get(t).compareTo(BigInteger.ONE)==0 ? "" + t : String.format("%d^%d", t, out.get(t))).collect(Collectors.joining("  ")), message);
            } catch (Exception e) {
                e.printStackTrace();
                sendMessage("Error: not a number", message);
            }
        }
    }

    private Map<BigInteger, BigInteger> powerifyBig(List<BigInteger> list) {
        Map<BigInteger, BigInteger> out = new HashMap<>();
        for (BigInteger l : list) {
            if (out.containsKey(l)) {
                out.put(l, out.get(l).add(BigInteger.ONE));
            } else {
                out.put(l, BigInteger.ONE);
            }
        }
        return out;
    }

    public void sendMessage(String message, Message target) {
        SendMessage sendMessageRequest = new SendMessage();
        sendMessageRequest.setText(message);
        sendMessageRequest.setChatId(target.getChatId().toString());
        try {
            sendMessage(sendMessageRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public static void main(String... args) {
        FactorizerBot factorizerBot;
        if (args.length > 0)
            factorizerBot = new FactorizerBot(args[0]);
        else
            factorizerBot = new FactorizerBot();
        FactorizerBot finalfactorizerBot = factorizerBot;
        new Thread(() -> {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
            try {
                telegramBotsApi.registerBot(finalfactorizerBot);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }).start();
    }

}















