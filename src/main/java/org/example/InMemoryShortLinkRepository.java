package org.example;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryShortLinkRepository implements ShortLinkRepository{

    private final Map<String, ShortLink> shortLinkMap = new ConcurrentHashMap<>();
    private final Map<UUID, Map<String, ShortLink>> userLinksMap = new ConcurrentHashMap<>();

    @Override
    public void save(ShortLink shortLink) {
        shortLinkMap.put(shortLink.getShortUrl(), shortLink);
        userLinksMap.computeIfAbsent(shortLink.getUserId(), k -> new ConcurrentHashMap<>())
                .put(shortLink.getOriginalUrl(), shortLink);
    }


    @Override
    public ShortLink findByShortUrl(String shortUrl) {
        return shortLinkMap.get(shortUrl);
    }

    @Override
    public ShortLink findByUserIdAndOriginalUrl(UUID userId, String originalUrl) {
        Map<String, ShortLink> userLinks = userLinksMap.get(userId);
        if (userLinks != null) {
            return userLinks.get(originalUrl);
        }
        return null;
    }

    @Override
    public void delete(String shortUrl) {
        ShortLink link = shortLinkMap.remove(shortUrl);
        if (link != null) {
            Map<String, ShortLink> userLinks = userLinksMap.get(link.getUserId());
            if (userLinks != null) {
                userLinks.remove(link.getOriginalUrl());
            }
        }
    }

    @Override
    public Collection<ShortLink> getAll() {
        return shortLinkMap.values();
    }
}