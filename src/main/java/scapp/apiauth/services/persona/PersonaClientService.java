package scapp.apiauth.services.persona;

import lombok.RequiredArgsConstructor;


import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import scapp.apiauth.config.PersonaServiceProperties;
import scapp.apiauth.dto.persona.PersonaCreateRequest;
import scapp.apiauth.dto.persona.PersonaResponse;
import scapp.apiauth.dto.response.ApiResponseDto;
import scapp.apiauth.interfaces.services.persona.IPersonaClientService;


@Service
@RequiredArgsConstructor
public class PersonaClientService implements


        IPersonaClientService {

    private final RestTemplate restTemplate;
    private final PersonaServiceProperties personaServiceProperties;

    @Override
    public PersonaResponse crearPersona(PersonaCreateRequest request) {
        String url = personaServiceProperties.getBaseUrl() + personaServiceProperties.getCreatePersonaPath();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", personaServiceProperties.getApiKey());
        HttpEntity<PersonaCreateRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<ApiResponseDto<PersonaResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<ApiResponseDto<PersonaResponse>>() {
                }
        );

        ApiResponseDto<PersonaResponse> body = response.getBody();


        if (body == null) {
            throw new RuntimeException("El microservicio de persona no devolvió respuesta.");
        }



        if (body.getData() == null) {
            throw new RuntimeException("El microservicio de persona no devolvió data.");
        }

        return body.getData();


    }
}