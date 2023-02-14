package com.naskoni.library.exporter;

import com.naskoni.library.entity.Book;
import com.naskoni.library.util.BooksCreator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@ExtendWith(SpringExtension.class)
class XmlFileExporterTest {

  private final XmlFileExporter exporter = new XmlFileExporter();

  @Test
  void exportXmlFileShouldSuccess() throws IOException {
    List<Book> books = BooksCreator.getBooks();
    byte[] export = exporter.export(books);
    assertNotNull(export);

    String asString = new String(export, StandardCharsets.UTF_8);
    assertTrue(asString.contains("name"));
    assertTrue(asString.contains("author"));
    assertTrue(asString.contains("1999"));
    assertTrue(asString.contains("1645712740"));
  }
}
