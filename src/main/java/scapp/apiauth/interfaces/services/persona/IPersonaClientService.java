package scapp.apiauth.interfaces.services.persona;

import org.springframework.data.domain.Page;
import scapp.apiauth.dto.persona.PersonaCreateRequest;
import scapp.apiauth.dto.persona.PersonaResponse;


public interface IPersonaClientService {

    PersonaResponse crearPersona(PersonaCreateRequest request);

}