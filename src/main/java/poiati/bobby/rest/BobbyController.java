package poiati.bobby.rest;


import poiati.bobby.PersonRepository;
import poiati.bobby.ConnectionManager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.beans.factory.annotation.Autowired;


@Controller
public class BobbyController {
    private final PersonRepository personRepository;
    private final ConnectionManager connectionManager;

    @Autowired
    public BobbyController(final PersonRepository personRepository, final ConnectionManager connectionManager) {
      this.personRepository = personRepository;
      this.connectionManager = connectionManager;
    }

    @RequestMapping(value="/foo", method = RequestMethod.GET)
    public void test() {
    }
}
