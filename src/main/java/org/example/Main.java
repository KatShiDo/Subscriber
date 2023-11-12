package org.example;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Main {
    private static JsonData jsonDataList = new JsonData();

    private static boolean isFirst = true;

    public static void main(String[] args) throws MqttException {
        var client = new MqttClient("tcp://127.0.0.1:1883", MqttClient.generateClientId());

        // Создание класса для формирования JSON
        var g = new Gson();
        // Объект для хранения данных
        var jsonData = new JsonData();

        // Создание колбэков для обработки событий, возникающих на клиенте
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("client lost connection " + cause);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                var payload = new String(message.getPayload());
                System.out.println(topic + ": " + payload);

                var param = Topic.fromValue(topic);

                if (param != null)
                    switch (param) {
                        case TEMPERATURE -> jsonData.setTemperature(Float.valueOf(payload));
                        case CO2 -> jsonData.setCO2(Integer.valueOf(payload));
                    }
                else
                    System.out.println("Not known topic");

                jsonDataList = new JsonData(jsonData);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                System.out.println("delivery complete " + token);
            }
        });

        client.connect();

        // Подписывание на топики
        for (var topic : Topic.values())
            client.subscribe(topic.getValue(), 1);

        var timer = new Thread(new Timer5sec());
        timer.start();
    }

    private static class Timer5sec implements Runnable {

        private final Gson g = new Gson();

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                writeUsingFiles(g.toJson(jsonDataList));
            }
        }
    }

    private static void writeUsingFiles(String data) {
        try {
            if (isFirst) {
                Files.write(Paths.get("data.json"), "[".getBytes(), StandardOpenOption.APPEND);
                isFirst = false;
            }
            Files.write(Paths.get("data.json"), data.getBytes(), StandardOpenOption.APPEND);
            Files.write(Paths.get("data.json"), ",".getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}