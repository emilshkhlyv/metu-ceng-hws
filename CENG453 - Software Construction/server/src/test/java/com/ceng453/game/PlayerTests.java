package com.ceng453.game;

import com.ceng453.game.model.Player;
import com.ceng453.game.repository.PlayerRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@RunWith(SpringRunner.class)
@SpringBootTest
public class PlayerTests {

    MockMvc mockMvc;

    @Autowired
    private PlayerRepository playerRepository;


    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    }

    @Test
    public void getAllPlayersTest() throws Exception {
        List<Player> playerList = playerRepository.findAll();
        mockMvc.perform(get("/api/player/getAll")).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.length()").value(playerList.size()));

    }

    @Test
    public void playerRegisterSuccessTest() throws Exception {
        mockMvc.perform(
                post("/api/player/register").contentType(MediaType.APPLICATION_JSON).content(
                        """
                        {
                            "username": "emilTest",
                            "password": "emilTest",
                            "email": "emilTest@emilTest.com"
                        }"""
                )).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Register operation is successful."));
        Optional<Player> optionalPlayer = playerRepository.findByUsername("emilTest");
        optionalPlayer.ifPresent(value -> playerRepository.delete(value));
    }

    @Test
    public void playerRegisterAlreadyExistsTest() throws Exception{
        mockMvc.perform(
                post("/api/player/register").contentType(MediaType.APPLICATION_JSON).content(
                                """
                                {
                                    "username": "emil",
                                    "password": "emilpassword",
                                    "email": "emil@emil.com"
                                }"""
                )).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Try a different username."));
    }

    @Test
    public void playerRegisterUsernameEmptyTest() throws Exception{
        mockMvc.perform(
                post("/api/player/register").contentType(MediaType.APPLICATION_JSON).content(
                        """
                        {
                            "username": "",
                            "password": "sedapassword",
                            "email": "seda@seda.com"
                        }"""
                )).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Username can't be empty."));
    }
    @Test
    public void playerRegisterPasswordEmptyTest() throws Exception{
        mockMvc.perform(
                post("/api/player/register").contentType(MediaType.APPLICATION_JSON).content(
                        """
                        {
                            "username": "seda",
                            "password": "",
                            "email": "seda@seda.com"
                        }"""
                )).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Password can't be empty."));
    }
    @Test
    public void playerRegisterEmailEmptyTest() throws Exception{
        mockMvc.perform(
                post("/api/player/register").contentType(MediaType.APPLICATION_JSON).content(
                        """
                        {
                            "username": "seda",
                            "password": "sedapassword",
                            "email": ""
                        }"""
                )).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("E-mail can't be empty."));
    }
    @Test
    public void playerLoginSuccessTest() throws Exception{
        mockMvc.perform(
                post("/api/player/login").contentType(MediaType.APPLICATION_JSON).content(
                        """
                        {
                            "username": "seda",
                            "password": "sedaseda"
                        }"""
                )).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void playerLoginIncorrectTest() throws Exception{
        mockMvc.perform(
                post("/api/player/login").contentType(MediaType.APPLICATION_JSON).content(
                        """
                        {
                            "username": "seda",
                            "password": "seda"
                        }"""
                )).andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void playerLoginUsernameEmptyTest() throws Exception{
        mockMvc.perform(
                post("/api/player/login").contentType(MediaType.APPLICATION_JSON).content(
                        """
                        {
                            "username": "",
                            "password": "sedaseda"
                        }"""
                )).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Username can't be empty."));
    }

    @Test
    public void playerLoginPasswordEmptyTest() throws Exception{
        mockMvc.perform(
                post("/api/player/login").contentType(MediaType.APPLICATION_JSON).content(
                        """
                        {
                            "username": "seda",
                            "password": ""
                        }"""
                )).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Password can't be empty."));
    }

    @Test
    public void getPlayerByUsernameTest() throws Exception{
        Optional<Player> optionalPlayer = playerRepository.findByUsername("seda");
        if(optionalPlayer.isPresent()){
            Player player=optionalPlayer.get();
            mockMvc.perform(
                    get("/api/player/getPlayer").queryParam("username","seda"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(jsonPath("$.id").value(player.getId()))
                    .andExpect(jsonPath("$.username").value(player.getUsername()))
                    .andExpect(jsonPath("$.email").value(player.getEmail()));
        }


    }

    @Test
    public void getPlayerByUsernameNotFoundTest() throws Exception{
        mockMvc.perform(
                get("/api/player/getPlayer").queryParam("username","notfound"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("User isn't found"));
    }

    @Test
    public void deletePlayerTest() throws Exception{
        Player player = new Player();
        player.setUsername("deletePlayer");
        player.setPassword("deletePlayer");
        player.setEmail("delete@player.com");
        playerRepository.save(player);
        Optional<Player> optionalPlayer = playerRepository.findByUsername("deletePlayer");
        if(optionalPlayer.isPresent()){
            mockMvc.perform(delete("/api/player/delete").queryParam("id",optionalPlayer.get().getId().toString()))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().string("Player is deleted successfully"));
        }

    }

    @Test
    public void deletePlayerNotFoundTest() throws Exception{
        mockMvc.perform(delete("/api/player/delete").queryParam("id","-1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Player isn't found."));
    }
}
