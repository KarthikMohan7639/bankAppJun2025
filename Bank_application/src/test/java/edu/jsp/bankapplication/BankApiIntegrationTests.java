package edu.jsp.bankapplication;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class BankApiIntegrationTests {

	private static final AtomicInteger SEQ = new AtomicInteger();

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper mapper;

	private JsonNode json(MvcResult r) throws Exception {
		return mapper.readTree(r.getResponse().getContentAsString());
	}

	private long createUser(String name) throws Exception {
		String body = """
				{"name":"%s","email":"%s%d@bank.test","password":"secret"}
				""".formatted(name, name.toLowerCase(), SEQ.incrementAndGet());
		MvcResult r = mvc.perform(post("/user/createUser").contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.password").doesNotExist())
				.andReturn();
		return json(r).get("id").asLong();
	}

	private long createAccount(long userId, double balance) throws Exception {
		MvcResult r = mvc.perform(post("/account/save").param("userId", String.valueOf(userId))
				.contentType(MediaType.APPLICATION_JSON).content("{\"balance\":" + balance + "}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.accountNumber", notNullValue()))
				.andExpect(jsonPath("$.userId").value(userId))
				.andReturn();
		return json(r).get("id").asLong();
	}

	@Test
	void userCrudAndValidation() throws Exception {
		long id = createUser("Alice");

		mvc.perform(get("/user/getUserByid").param("id", String.valueOf(id)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Alice"));

		mvc.perform(get("/user/getbyname").param("name", "lic"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name").value("Alice"));

		mvc.perform(post("/user/createUser").contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"\",\"email\":\"bad\",\"password\":\"x\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.fieldErrors.email", notNullValue()));

		mvc.perform(get("/user/getUserByid").param("id", "999999"))
				.andExpect(status().isNotFound());

		mvc.perform(delete("/user/delete/" + id)).andExpect(status().isOk());
		mvc.perform(get("/user/getUserByid").param("id", String.valueOf(id))).andExpect(status().isNotFound());
	}

	@Test
	void transactionsUpdateBalanceAndTransferIsChecked() throws Exception {
		long userId = createUser("Bob");
		long a1 = createAccount(userId, 1000);
		long a2 = createAccount(userId, 0);

		mvc.perform(post("/transaction/save").param("accountId", String.valueOf(a1))
				.contentType(MediaType.APPLICATION_JSON).content("{\"amount\":200,\"type\":\"DEBIT\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", notNullValue()))
				.andExpect(jsonPath("$.accountId").value(a1))
				.andExpect(jsonPath("$.date", notNullValue()));
		mvc.perform(get("/account/balance/" + a1)).andExpect(content().string("800.0"));

		mvc.perform(put("/account/fundTransfer/" + a1 + "/" + a2 + "/300")).andExpect(status().isOk());
		mvc.perform(get("/account/balance/" + a1)).andExpect(content().string("500.0"));
		mvc.perform(get("/account/balance/" + a2)).andExpect(content().string("300.0"));

		mvc.perform(put("/account/fundTransfer/" + a2 + "/" + a1 + "/5000"))
				.andExpect(status().isBadRequest());
		mvc.perform(put("/account/withdraw/" + a2 + "/-5")).andExpect(status().isBadRequest());

		// opening deposit + debit + transfer out + transfer in
		mvc.perform(get("/transaction/fetchAll/" + userId)).andExpect(jsonPath("$", hasSize(4)));
		mvc.perform(get("/transaction/getByAmount/" + userId + "/250/1000")).andExpect(jsonPath("$", hasSize(3)));

		LocalDateTime now = LocalDateTime.now();
		mvc.perform(get("/transaction/fetchByAccount/" + userId + "/" + now.minusHours(1).withNano(0) + "/"
				+ now.plusHours(1).withNano(0)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(4)));
	}

	@Test
	void loanLifecycle() throws Exception {
		long userId = createUser("Carol");
		long account = createAccount(userId, 0);

		MvcResult r = mvc.perform(post("/loan/applyLoan").param("userId", String.valueOf(userId))
				.contentType(MediaType.APPLICATION_JSON).content("{\"amount\":1000}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.status").value("PENDING"))
				.andExpect(jsonPath("$.userId").value(userId))
				.andReturn();
		long loanId = json(r).get("id").asLong();

		mvc.perform(put("/loan/disburse/" + loanId + "/" + account)).andExpect(status().isBadRequest());
		mvc.perform(put("/loan/approve/" + loanId)).andExpect(jsonPath("$.status").value("APPROVED"));
		mvc.perform(put("/loan/disburse/" + loanId + "/" + account))
				.andExpect(jsonPath("$.status").value("DISBURSED"))
				.andExpect(jsonPath("$.balance").value(1000.0));
		mvc.perform(get("/account/balance/" + account)).andExpect(content().string("1000.0"));

		mvc.perform(put("/loan/repay/" + loanId + "/400").param("accountId", String.valueOf(account)))
				.andExpect(jsonPath("$.balance").value(600.0));
		mvc.perform(put("/loan/repay/" + loanId + "/700")).andExpect(status().isBadRequest());
		mvc.perform(put("/loan/repay/" + loanId + "/600"))
				.andExpect(jsonPath("$.status").value("CLOSED"));
		mvc.perform(get("/loan/status/" + loanId)).andExpect(content().string("CLOSED"));
		mvc.perform(get("/account/balance/" + account)).andExpect(content().string("600.0"));

		mvc.perform(get("/loan/getLoanDetailsByUserId/" + userId)).andExpect(jsonPath("$", hasSize(1)));
		mvc.perform(get("/user/getUserByid").param("id", String.valueOf(userId)))
				.andExpect(jsonPath("$.loans", hasSize(1)))
				.andExpect(jsonPath("$.accounts", hasSize(1)));
	}
}
