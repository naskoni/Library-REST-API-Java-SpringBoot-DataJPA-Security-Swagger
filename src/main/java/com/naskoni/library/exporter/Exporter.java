package com.naskoni.library.exporter;

import com.naskoni.library.entity.Book;

import java.io.IOException;
import java.util.List;

/**
 * Abstract interface implemented by Exporter objects.
 *
 * @author Atanas Atanasov
 * @version 1.0.0
 */
public interface Exporter {

  /**
   * The property "type" must be declared in extending classes and initialized with values
   * representing the type of exporting file. The returned value will be used to match the Exporter
   * type (@PathVariable) from URL.
   *
   * @return the type of the Exporter
   */
  String getType();

  /**
   * Execute the export operation
   *
   * @param books the book records in DB that will be exported
   * @return byte array containing the result
   * @throws IOException
   */
  byte[] export(List<Book> books) throws IOException;
}
