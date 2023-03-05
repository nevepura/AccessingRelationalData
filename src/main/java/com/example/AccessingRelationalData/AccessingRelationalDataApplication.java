package com.example.AccessingRelationalData;

import com.example.AccessingRelationalData.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * App that performs CRUD operations on a table (H2 database).
 */
@SpringBootApplication
public class AccessingRelationalDataApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(AccessingRelationalDataApplication.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {

		SpringApplication.run(AccessingRelationalDataApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Create
		jdbcTemplate.execute("DROP TABLE customers IF EXISTS");
		jdbcTemplate.execute("CREATE TABLE customers(" +
				"id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");

		// Insert
		List<Object[]> splitUpNames = Arrays.asList("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long").stream()
				.map(name -> name.split(" "))
				.collect(Collectors.toList());

		jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", splitUpNames);

		// Select
		log.info("Querying for all records");
		printTable();

		// Update
		String updateQuery = "UPDATE customers SET first_name = 'Bob'";
		jdbcTemplate.update(updateQuery);
		log.info("Querying after update");
		printTable();

		// Delete
		log.info ("Deleting some records");
		String deleteQuery = "DELETE FROM customers where last_name = 'Dean'";
		jdbcTemplate.update(deleteQuery);
		log.info("Querying after delete");
		printTable();

		// Clear
		log.info("Clear table");
		String clearQuery = "DELETE FROM customers";
		jdbcTemplate.update(clearQuery);
		log.info("Querying after clear");
		printTable();
	}

	/**
	 * Print all the rows in the table
	 */
	void printTable(){
		String selectQuery = "SELECT id, first_name, last_name FROM customers";
		jdbcTemplate.query(
			selectQuery,
			(ResultSet rs) -> {
				while (!rs.isAfterLast()) {
					log.info(String.format("id %d, name %s, surname %s", rs.getLong(1), rs.getString(2), rs.getString(3)));
					rs.next();
				}
			});
	}
}
