package com.ceng453.game;

import com.ceng453.game.model.SessionRecord;
import com.ceng453.game.repository.SessionRecordRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SessionRecordTests {
    MockMvc mockMvc;
    @Autowired
    private SessionRecordRepository sessionRecordRepository;


    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    }
    @Test
    public void listLeaderboardTest() throws Exception {
        List<SessionRecord> sessionRecordList = sessionRecordRepository.findAll();
        mockMvc.perform(get("/api/sessionRecord/listLeaderboard")).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.length()").value(sessionRecordList.size()));
    }
    @Test
    public void listLeaderboardMonthly() throws Exception{
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusDays(30);
        List<SessionRecord> sessionRecordList = sessionRecordRepository.findAllByDateAfter(oneMonthAgo);
        mockMvc.perform(get("/api/sessionRecord/listLeaderboardMonthly")).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.length()").value(sessionRecordList.size()));
    }
    @Test
    public void listLeaderboardWeekly() throws Exception{
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        List<SessionRecord> sessionRecordList = sessionRecordRepository.findAllByDateAfter(oneWeekAgo);
        mockMvc.perform(get("/api/sessionRecord/listLeaderboardWeekly")).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.length()").value(sessionRecordList.size()));
    }
    @Test
    public void matchMakeTest() throws Exception {
        mockMvc.perform(get("/api/sessionRecord/matchMake").queryParam("playerId","1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
