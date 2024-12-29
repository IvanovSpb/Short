package org.example;

import java.util.UUID;

public class UrlShortenerController {
    private final ShortLinkService shortLinkService;

    public UrlShortenerController(ShortLinkService shortLinkService) {
        this.shortLinkService = shortLinkService;
    }

    public ShortLink createShortLink(String originalUrl, Integer maxClicks, UUID userId) {
        return shortLinkService.createShortLink(originalUrl, maxClicks != null ? maxClicks : Integer.MAX_VALUE, userId);
    }

    public String redirect(String shortUrl) throws ShortLinkException {
        return shortLinkService.redirectToOriginalUrl(shortUrl);
    }

    public ShortLink findByUserIdAndOriginalUrl(UUID userId, String originalUrl) {
        return shortLinkService.findByUserIdAndOriginalUrl(userId, originalUrl);
    }
}