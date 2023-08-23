package com.human.urlShortner.service;

import com.human.urlShortner.model.Url;
import com.human.urlShortner.model.UrlDto;
import org.springframework.stereotype.Service;

@Service
public interface UrlService {

    Url generateShortLink(UrlDto urlDto);
    Url persistShortLink(Url url);

    Url getEncodedUrl(String url);

    void deleteShortLink(Url url);
}
