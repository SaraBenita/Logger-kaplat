package com.javaguides.springboot.skills.Controller;

import com.javaguides.springboot.skills.model.Book;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class ServerUtils {
	private static int id;
	private List<Book> setBooks;

	public ServerUtils()
	{
		this.id = 0;  //מתחיל מ-1 בפועל
		setBooks = new ArrayList<>();
	}

	public void setId(int id) {
		ServerUtils.id = id;
	}
	public int getId() {
		return id;
	}
	public List<Book> getSetBooks() {
		return setBooks;
	}
	public void setSetBooks(List<Book> setBooks) {
		this.setBooks = setBooks;
	}
}
