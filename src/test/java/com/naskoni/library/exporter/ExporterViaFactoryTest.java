package com.naskoni.library.exporter;

import com.naskoni.library.config.WebAppConfig;
import com.naskoni.library.entity.Book;
import com.naskoni.library.exception.NotFoundException;
import com.naskoni.library.util.BooksCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WebAppConfig.class)
@WebAppConfiguration
public class ExporterViaFactoryTest {

  @Autowired private ExporterFactory exporterFactory;

  @Test
  public void exportCsvFileShouldSuccess() throws IOException {
    List<Book> books = BooksCreator.getBooks();
    Exporter exporter = exporterFactory.newInstance("csv");
    byte[] export = exporter.export(books);
    assertNotNull(export);

    String asString = new String(export, "UTF-8");
    assertTrue(asString.contains("author,created,id,isbn,name,updated,year"));
    assertTrue(asString.contains("author,,,1645712740,name,,1999"));
  }

  @Test
  public void exportXmlFileShouldThrow() {
    assertThrows(NotFoundException.class, () -> exporterFactory.newInstance("xml"));
  }
}
