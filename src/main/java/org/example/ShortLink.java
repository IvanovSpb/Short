package org.example;

import java.time.LocalDateTime;
import java.util.UUID;

public class ShortLink {
    private String originalUrl;
    private String shortUrl;
    private UUID userId;
    private int maxClicks;
    private LocalDateTime expirationTime;
    private int clicks;
    private ShortLinkStatus status;

    public ShortLink(String originalUrl, String shortUrl, UUID userId, int maxClicks, LocalDateTime expirationTime) {
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
        this.userId = userId;
        this.maxClicks = maxClicks;
        this.expirationTime = expirationTime;
        this.clicks = 0;
        this.status = ShortLinkStatus.ACTIVE;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public UUID getUserId() {
        return userId;
    }

    public int getMaxClicks() {
        return maxClicks;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public int getClicks() {
        return clicks;
    }

    public void incrementClicks() {
        this.clicks++;
    }

    public ShortLinkStatus getStatus() {
        return status;
    }

    public void setStatus(ShortLinkStatus status) {
        this.status = status;
    }
}