package com.human.urlShortner.controller;

import com.human.urlShortner.model.Url;
import com.human.urlShortner.model.UrlDto;
import com.human.urlShortner.model.UrlErrorResponseDto;
import com.human.urlShortner.model.UrlResponseDto;
import com.human.urlShortner.service.UrlService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
public class UrlController {

    @Autowired
    private UrlService urlService;


    @PostMapping("/generate")
    public ResponseEntity<?> generateShortLink(@RequestBody UrlDto urlDto){
        Url urlToReturn = urlService.generateShortLink(urlDto);

        if(urlToReturn !=null){
            UrlResponseDto urlResponseDto = new UrlResponseDto();
            urlResponseDto.setOriginalUrl(urlToReturn.getOriginalUrl());
            urlResponseDto.setExpirationDate(urlToReturn.getExpirationDate());
            urlResponseDto.setShortLink(urlToReturn.getShortLink());
            return new ResponseEntity<>(urlResponseDto, HttpStatus.OK);
        }
        UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
        urlErrorResponseDto.setStatus("404");
        urlErrorResponseDto.setError("There was an error processing your request. please try again.");
        return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto,HttpStatus.OK);
    }

    @GetMapping("{shortLink}")
    public ResponseEntity<?> redirectToOriginalUrl(@PathVariable String shortLink, HttpServletResponse response) throws IOException {

        if(StringUtils.isEmpty(shortLink)){

            UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
            urlErrorResponseDto.setError("Invalid url!");
            urlErrorResponseDto.setStatus("400");
            return new ResponseEntity<>(urlErrorResponseDto,HttpStatus.OK);
        }
        Url urlToReturn = urlService.getEncodedUrl(shortLink);

        if(urlToReturn == null){
            UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
            urlErrorResponseDto.setStatus("400");
            urlErrorResponseDto.setError("Url does not exist or it might have expired!");
            return new ResponseEntity<>(urlErrorResponseDto, HttpStatus.OK);

        }
        if(urlToReturn.getExpirationDate().isBefore(LocalDateTime.now())){
            urlService.deleteShortLink(urlToReturn);
            UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
            urlErrorResponseDto.setError("Url Expired. Please try to generating a fresh one");
            urlErrorResponseDto.setStatus("200");
            return new ResponseEntity<>(urlErrorResponseDto, HttpStatus.OK);
        }
        response.sendRedirect(urlToReturn.getOriginalUrl());
        return null;

    }

}
