package com.human.urlShortner.service;

import com.google.common.hash.Hashing;
import com.human.urlShortner.model.Url;
import com.human.urlShortner.model.UrlDto;
import com.human.urlShortner.model.UrlResponseDto;
import com.human.urlShortner.repository.UrlRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component

public class UrlServiceImpl implements UrlService{

    private static final Logger logger = LoggerFactory.getLogger(UrlServiceImpl.class);

    @Autowired
    private UrlRepository urlRepository;
    @Override
    public Url  generateShortLink(UrlDto urlDto) {

        if(StringUtils.isNotEmpty(urlDto.getUrl())){
            String encodedUrl = encodedUrl(urlDto.getUrl());
            Url urlToPersist = new Url();
            urlToPersist.setCreationDate(LocalDateTime.now());
            urlToPersist.setOriginalUrl(urlDto.getUrl());
            urlToPersist.setShortLink(encodedUrl);
            urlToPersist.setExpirationDate(getExpirationDate(urlDto.getExpirationDate(),urlToPersist.getCreationDate()));
            Url urlToReturn = persistShortLink(urlToPersist);

            if(urlToReturn != null)
                return urlToReturn;
            return null;
        }
        return null;
    }

    private LocalDateTime getExpirationDate(String expirationDate, LocalDateTime creationDate) {
        if(StringUtils.isBlank(expirationDate)){
            return creationDate.plusSeconds(5000);
        }
        LocalDateTime expirationDateToReturn = LocalDateTime.parse(expirationDate);
        return expirationDateToReturn;
    }

    private String encodedUrl(String url) {
        String encodedUrl="";
        LocalDateTime time = LocalDateTime.now();
        encodedUrl = Hashing.murmur3_32()
                .hashString(url.concat(time.toString()), StandardCharsets.UTF_8)
                .toString();
        return encodedUrl;
    }

    @Override
    public Url persistShortLink(Url url) {
        Url urlToReturn = urlRepository.save(url);
        return urlToReturn;
    }

    @Override
    public Url getEncodedUrl(String url) {
        Url urlToReturn = urlRepository.findByShortLink(url);
        return urlToReturn;
    }

    @Override
    public void deleteShortLink(Url url) {
        urlRepository.delete(url);
    }
}
