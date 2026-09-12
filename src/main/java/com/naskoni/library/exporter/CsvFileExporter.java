package com.naskoni.library.exporter;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.naskoni.library.entity.Book;

import java.io.IOException;
import java.util.List;

/**
 * Exports book records as CSV file
 */
public class CsvFileExporter implements Exporter {

  private static final String CSV = "csv";
  private static final char COLUMN_SEPARATOR_CSV = ',';

  @Override
  public String getType() {
    return CSV;
  }

  /**
   * Exports books as CSV
   *
   * @param books books to export
   * @return byte array representing the CSV content
   * @throws IOException if the CSV cannot be generated
   */
  @Override
  public byte[] export(List<Book> books) throws IOException {
    CsvMapper csvMapper = new CsvMapper();

    CsvSchema schema = csvMapper
        .schemaFor(Book.class)
        .withoutQuoteChar()
        .withHeader()
        .withColumnSeparator(COLUMN_SEPARATOR_CSV);

    return csvMapper
        .writer(schema)
        .writeValueAsBytes(books);
  }
}