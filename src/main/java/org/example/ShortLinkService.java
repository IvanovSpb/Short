package org.example;

import java.time.LocalDateTime;
import java.util.UUID;
import java.awt.*;
import java.net.URI;
import java.io.IOException;
import java.net.URISyntaxException;

public class ShortLinkService {
    private final ShortLinkRepository shortLinkRepository;
    private final NotificationService notificationService;
    private final Base62Encoder base62Encoder;
    private static final int SHORT_LINK_LENGTH = 8;

    public ShortLinkService(ShortLinkRepository shortLinkRepository, NotificationService notificationService, Base62Encoder base62Encoder) {
        this.shortLinkRepository = shortLinkRepository;
        this.notificationService = notificationService;
        this.base62Encoder = base62Encoder;
    }

    public ShortLink createShortLink(String originalUrl, int maxClicks, UUID userId) {
        String shortUrl = generateUniqueShortUrl();
        LocalDateTime expirationTime = LocalDateTime.now().plusDays(1);
        ShortLink shortLink = new ShortLink(originalUrl, shortUrl, userId, maxClicks, expirationTime);
        shortLinkRepository.save(shortLink);
        return shortLink;
    }

    public String redirectToOriginalUrl(String shortUrl) throws ShortLinkException {
        ShortLink shortLink = shortLinkRepository.findByShortUrl(shortUrl);
        if (shortLink == null) {
            throw new ShortLinkNotFoundException("Short link not found: " + shortUrl);
        }
        checkLinkValidity(shortLink);
        shortLink.incrementClicks();
        shortLinkRepository.save(shortLink);

        try {
            Desktop.getDesktop().browse(new URI(shortLink.getOriginalUrl()));
        } catch (IOException | URISyntaxException e) {
            System.err.println("Error opening the browser:" + e.getMessage());
        }
        return shortLink.getOriginalUrl();
    }

    private void checkLinkValidity(ShortLink shortLink) throws ShortLinkException {
        if (shortLink.getStatus() != ShortLinkStatus.ACTIVE) {
            throw new ShortLinkExpiredException("Short link is not active: " + shortLink.getShortUrl() + ", status: " + shortLink.getStatus());
        }
        if (shortLink.getClicks() >= shortLink.getMaxClicks() ) {
            shortLink.setStatus(ShortLinkStatus.MAX_CLICKS_REACHED);
            shortLinkRepository.save(shortLink);
            notificationService.sendNotification(shortLink.getUserId(), "Max clicks reached for short link: " + shortLink.getShortUrl());
            throw new ShortLinkExpiredException("Max clicks reached for short link: " + shortLink.getShortUrl());
        }
        if (shortLink.getExpirationTime().isBefore(LocalDateTime.now())) {
            shortLink.setStatus(ShortLinkStatus.EXPIRED);
            shortLinkRepository.save(shortLink);
            notificationService.sendNotification(shortLink.getUserId(), "Short link expired: " + shortLink.getShortUrl());
            throw new ShortLinkExpiredException("Short link expired: " + shortLink.getShortUrl());
        }
    }

    public ShortLink findByUserIdAndOriginalUrl(UUID userId, String originalUrl) {
        return shortLinkRepository.findByUserIdAndOriginalUrl(userId, originalUrl);
    }

    private String generateUniqueShortUrl() {
        long id = System.nanoTime();
        String shortUrl = base62Encoder.encode(id);
        if (shortUrl.length() > SHORT_LINK_LENGTH) {
            shortUrl = shortUrl.substring(shortUrl.length() - SHORT_LINK_LENGTH);
        }
        while (shortLinkRepository.findByShortUrl(shortUrl) != null) {
            id++;
            shortUrl = base62Encoder.encode(id);
            if (shortUrl.length() > SHORT_LINK_LENGTH) {
                shortUrl = shortUrl.substring(shortUrl.length() - SHORT_LINK_LENGTH);
            }
        }
        return shortUrl;
    }
}
