package org.example;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        Base62Encoder base62Encoder = new Base62Encoder();
        InMemoryShortLinkRepository shortLinkRepository = new InMemoryShortLinkRepository();
        NotificationService notificationService = new NotificationService();
        ShortLinkService shortLinkService = new ShortLinkService(shortLinkRepository, notificationService, base62Encoder);
        UrlShortenerController controller = new UrlShortenerController(shortLinkService);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            shortLinkRepository.getAll().forEach(shortLink -> {
                try {
                    if (shortLink.getExpirationTime().isBefore(LocalDateTime.now())) {
                        shortLink.setStatus(ShortLinkStatus.EXPIRED);
                        shortLinkRepository.save(shortLink);
                        notificationService.sendNotification(shortLink.getUserId(), "Short link expired: " + shortLink.getShortUrl());
                        shortLinkRepository.delete(shortLink.getShortUrl());
                    }
                } catch (Exception e) {
                    System.out.println("Error processing link expiration: " + e.getMessage());
                }
            });
        }, 0, 10, TimeUnit.SECONDS);

        UUID user1Id = UUID.randomUUID();
        UUID user2Id = UUID.randomUUID();

        // Создание сокращенных ссылок
        ShortLink link1 = controller.createShortLink("https://www.example.com/long-url-1", 2, user1Id);
        ShortLink link2 = controller.createShortLink("https://www.example.com/long-url-2", 3, user1Id);
        ShortLink link3 = controller.createShortLink("https://www.example.com/long-url-1", 2, user2Id);

        System.out.println("User 1 link 1: " + link1.getShortUrl());
        System.out.println("User 1 link 2: " + link2.getShortUrl());
        System.out.println("User 2 link 1: " + link3.getShortUrl());
        try {
            //  Переходы по ссылкам
            System.out.println("Redirecting from link 1: " + controller.redirect(link1.getShortUrl()));
            System.out.println("Redirecting from link 1: " + controller.redirect(link1.getShortUrl()));
            System.out.println("Redirecting from link 1: " + controller.redirect(link1.getShortUrl()));
        } catch (ShortLinkException e) {
            System.out.println(e.getMessage());
        }

        try {
            System.out.println("Redirecting from link 2: " + controller.redirect(link2.getShortUrl()));
            System.out.println("Redirecting from link 2: " + controller.redirect(link2.getShortUrl()));
            System.out.println("Redirecting from link 2: " + controller.redirect(link2.getShortUrl()));
            System.out.println("Redirecting from link 2: " + controller.redirect(link2.getShortUrl()));
        } catch (ShortLinkException e) {
            System.out.println(e.getMessage());
        }

        try {
            System.out.println("Redirecting from link 3: " + controller.redirect(link3.getShortUrl()));
            System.out.println("Redirecting from link 3: " + controller.redirect(link3.getShortUrl()));
            System.out.println("Redirecting from link 3: " + controller.redirect(link3.getShortUrl()));
        } catch (ShortLinkException e) {
            System.out.println(e.getMessage());
        }
        try{
            Thread.sleep(12000);
            System.out.println("Redirecting from link 3 after 12 sec: " + controller.redirect(link3.getShortUrl()));
        }
        catch (InterruptedException e){
            System.out.println(e.getMessage());
        }
        catch (ShortLinkException e) {
            System.out.println(e.getMessage());
        }
        scheduler.shutdown();
    }
}