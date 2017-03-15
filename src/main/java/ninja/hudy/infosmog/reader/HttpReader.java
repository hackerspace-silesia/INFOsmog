package ninja.hudy.infosmog.reader;

import ninja.hudy.infosmog.exception.HttpCommunicationException;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;

public abstract class HttpReader extends Reader {
    @Autowired
    private RestTemplate restTemplate;

    protected String sendPost(@NotBlank String address, @NotNull MultiValueMap<String, String> params) throws HttpCommunicationException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(address, request, String.class);

        if(!HttpStatus.OK.equals(response.getStatusCode())) {
            throw new HttpCommunicationException("Send http request to " + address + " failed with code: " + response.getStatusCodeValue());
        }

        return response.getBody();
    }

    protected String sendGet(@NotBlank String address) throws HttpCommunicationException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<String> response = restTemplate.getForEntity(address, String.class);

        if(!HttpStatus.OK.equals(response.getStatusCode())) {
            throw new HttpCommunicationException("Send http request to " + address + " failed with code: " + response.getStatusCodeValue());
        }

        return response.getBody();
    }
}
