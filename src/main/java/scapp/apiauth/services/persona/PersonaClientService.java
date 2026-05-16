package scapp.apiauth.services.persona;

import lombok.RequiredArgsConstructor;


import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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

    @Override
    public PersonaResponse obtenerPorEmail(String correo) {
        String url = personaServiceProperties.getBaseUrl() + "/api/personas/buscar?email=" + correo;

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", personaServiceProperties.getApiKey());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            System.out.println("[PersonaClient] Consultando URL: " + url);
            ResponseEntity<ApiResponseDto<PersonaResponse>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<ApiResponseDto<PersonaResponse>>() {}
            );

            ApiResponseDto<PersonaResponse> body = response.getBody();
            if (body != null && body.getData() != null) {
                System.out.println("[PersonaClient] Persona recuperada con éxito. ID: " + body.getData().getId());
                return body.getData();
            }
        } catch (HttpClientErrorException.NotFound e) {
            System.out.println("[PersonaClient] La persona no existe en la DB (404 esperado para usuarios nuevos) para el correo: " + correo);
            return null;
        } catch (Exception e) {
            // !!! ESTO VA A REVELAR EL ERROR REAL EN TU CONSOLA !!!
            System.err.println("[PersonaClient] ERROR CRÍTICO al comunicarse con el microservicio de personas:");
            e.printStackTrace();
            return null;
        }

        return null;
    }

}