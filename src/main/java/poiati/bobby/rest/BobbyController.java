package poiati.bobby.rest;


import poiati.bobby.PersonRepository;
import poiati.bobby.Person;
import poiati.bobby.ConnectionManager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.PropertyNamingStrategy;

// TODO REST Error Handle

@Controller
@RequestMapping(value="/api/person", headers = "Accept=application/json")
public class BobbyController {
    PersonRepository personRepository;
    ConnectionManager connectionManager;

    @Autowired
    public BobbyController(final PersonRepository personRepository, final ConnectionManager connectionManager) {
      this.personRepository = personRepository;
      this.connectionManager = connectionManager;
    }

    @RequestMapping(value="/", method = RequestMethod.POST)
    public ResponseEntity<String> create(final @RequestBody String json) throws IOException {
        final JsonNode jsonMap = this.parseJson(json);
        System.out.println(jsonMap.get("facebook_id").getIntValue());

        this.personRepository.create(new Person(jsonMap.get("name").getTextValue(), 
                                                jsonMap.get("facebook_id").getIntValue()));

        return new ResponseEntity<String>(new HttpHeaders(), HttpStatus.CREATED);
    }

    @RequestMapping(value="/{facebookId}/friends/", method = RequestMethod.POST)
    public ResponseEntity<String> createConnection(final @PathVariable Integer facebookId, 
                                                   final @RequestBody String json) throws IOException {
        final JsonNode jsonMap = this.parseJson(json);

        this.connectionManager.connectFriend(facebookId, jsonMap.get("facebook_id").getIntValue());
        return new ResponseEntity<String>(new HttpHeaders(), HttpStatus.CREATED);
    }

    @RequestMapping(value="/{facebookId}/friends/", method = RequestMethod.GET)
    public ResponseEntity<String> friends(final @PathVariable Integer facebookId) throws IOException {
        final Set<Person> friends = this.personRepository.friendsFor(facebookId);
        return connections(friends);
    }

    @RequestMapping(value="/{facebookId}/friends/recommendations", method = RequestMethod.GET)
    public ResponseEntity<String> suggestions(final @PathVariable Integer facebookId) throws IOException {
        final Set<Person> suggestions = this.personRepository.suggestionsFor(facebookId);
        return connections(suggestions);
    }

    private ResponseEntity<String> connections(final Set<Person> connections) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        final String jsonString = mapper.writeValueAsString(new PersonsJsonMapper(connections));

        return new ResponseEntity<String>(jsonString, new HttpHeaders(), HttpStatus.OK);
    }

    private JsonNode parseJson(final String json) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, JsonNode.class);
    }
}
