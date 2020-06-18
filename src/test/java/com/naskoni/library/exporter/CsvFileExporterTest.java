package com.naskoni.library.exporter;

import com.naskoni.library.entity.Book;
import com.naskoni.library.util.BooksCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
public class CsvFileExporterTest {

  private CsvFileExporter exporter = new CsvFileExporter();

  @Test
  public void exportCsvFileShouldSuccess() throws IOException {
    List<Book> books = BooksCreator.getBooks();
    byte[] export = exporter.export(books);
    assertNotNull(export);

    String asString = new String(export, "UTF-8");
    assertTrue(asString.contains("author,created,id,isbn,name,updated,year"));
    assertTrue(asString.contains("author,,,1645712740,name,,1999"));
  }
}
