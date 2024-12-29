package org.example;

import java.util.Collection;
import java.util.UUID;

public interface ShortLinkRepository {
    void save(ShortLink shortLink);
    ShortLink findByShortUrl(String shortUrl);
    ShortLink findByUserIdAndOriginalUrl(UUID userId, String originalUrl);
    void delete(String shortUrl);
    Collection<ShortLink> getAll();
}