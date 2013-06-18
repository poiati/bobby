package poiati.bobby.rest;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.kubek2k.springockito.annotations.ReplaceWithMock;

import static org.mockito.Mockito.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import static com.jayway.jsonassert.JsonAssert.*;

import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

import poiati.bobby.PersonRepository;
import poiati.bobby.ConnectionManager;
import poiati.bobby.Person;
import poiati.bobby.FacebookIdAlreadyExistsException;
import poiati.bobby.ResourceNotFoundException;

// TODO Test edge cases
// TODO DRY

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = "/bobby-servlet-test.xml")
public class BobbyControllerTest {
    @Autowired
    WebApplicationContext wac;

    @Autowired
    BobbyController controller;

    PersonRepository personRepository;
    ConnectionManager connectionManager;

    MockMvc mockMvc;

    String name = "Ned";
    Integer facebookId = 123;

    @Before
    public void setUp() {
        this.mockCollaborators();
        this.mockMvc = webAppContextSetup(this.wac).build();
    }

    @Test
    public void testPostCreate() throws Exception {
        this.doPost(
            "/api/person/", 
            "{\"name\": \"" + this.name + "\", \"facebook_id\": " + this.facebookId + "}"
        ).andExpect(status().is(201));

        verify(this.personRepository).create(new Person(name, facebookId));
    }

    @Test
    public void testPostCreateInvalidJson() throws Exception {
        this.doPost(
            "/api/person/", 
            "{\"facebook_id\": " + this.facebookId + "}"
        ).andExpect(status().is(400));
    }

    @Test
    public void testPostCreateSameFacebookId() throws Exception {
        doThrow(new FacebookIdAlreadyExistsException(this.facebookId))
            .when(this.personRepository).create(new Person(name, facebookId));

        this.doPost(
            "/api/person/", 
            "{\"name\": \"" + this.name + "\", \"facebook_id\": " + this.facebookId + "}"
        ).andExpect(status().is(400));
    }

    @Test
    public void testPostCreateConnection() throws Exception {
        this.doPost(
            "/api/person/321/friends/", 
            "{\"facebook_id\": " + this.facebookId + "}"
        ).andExpect(status().is(201));

        verify(this.connectionManager).connectFriend(321, 123);
    }

    @Test
    public void testGetFriends() throws Exception {
        when(this.personRepository.friendsFor(this.facebookId))
            .thenReturn(this.personsFixture());

        this.doGet("/api/person/123/friends/")
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.objects[*].name")
                .value(hasItems("Tyrion", "James")))
            .andExpect(jsonPath("$.objects[*].facebook_id")
                .value(hasItems(111, 222)));
    }

    @Test
    public void testGetFriendsPersonNotFound() throws Exception {
        when(this.personRepository.friendsFor(this.facebookId))
            .thenThrow(new ResourceNotFoundException());

        this.doGet("/api/person/123/friends/")
            .andExpect(status().is(404));
    }

    @Test
    public void testGetFriendsEmpty() throws Exception {
        when(this.personRepository.friendsFor(this.facebookId))
            .thenReturn(new HashSet());

        this.doGet("/api/person/123/friends/")
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.objects").value(is(collectionWithSize(equalTo(0)))));

    }

    @Test
    public void testGetSuggestions() throws Exception {
        when(this.personRepository.suggestionsFor(this.facebookId))
            .thenReturn(this.personsFixture());

        this.doGet("/api/person/123/friends/recommendations/")
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.objects[*].name")
                .value(hasItems("Tyrion", "James")))
            .andExpect(jsonPath("$.objects[*].facebook_id")
                .value(hasItems(111, 222)));
    }

    @Test
    public void testGetSuggestionsNotFound() throws Exception {
        when(this.personRepository.suggestionsFor(this.facebookId))
            .thenThrow(new ResourceNotFoundException());

        this.doGet("/api/person/123/friends/recommendations/")
            .andExpect(status().is(404));

    }

    @Test
    public void testGetSuggestionsEmpty() throws Exception {
        when(this.personRepository.suggestionsFor(this.facebookId))
            .thenReturn(new HashSet());

        this.doGet("/api/person/123/friends/recommendations/")
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.objects").value(is(collectionWithSize(equalTo(0)))));

    }

    @Test
    public void testUpdateRecommendations() throws Exception {
        this.doPut("/api/recommendations/update/")
            .andExpect(status().is(200));

        verify(this.connectionManager).updateSuggestions();
    }

    private ResultActions doPost(String uri, String content) throws Exception {
        return this.mockMvc.perform(post(uri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));
    }

    private ResultActions doGet(String uri) throws Exception {
        return this.mockMvc.perform(get(uri)
                .accept(MediaType.APPLICATION_JSON));
    }

    private ResultActions doPut(String uri) throws Exception {
        return this.mockMvc.perform(put(uri));
    }

    private void mockCollaborators() {
        this.personRepository = mock(poiati.bobby.PersonRepository.class);
        this.connectionManager = mock(poiati.bobby.ConnectionManager.class);
        this.controller.personRepository = this.personRepository;
        this.controller.connectionManager = this.connectionManager;
    }

    private Set<Person> personsFixture() {
        Set<Person> persons = new HashSet<Person>();
        persons.add(new Person("Tyrion", 111));
        persons.add(new Person("James", 222));
        return persons;
    }
}
