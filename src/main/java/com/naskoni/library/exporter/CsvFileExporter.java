package com.naskoni.library.exporter;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.naskoni.library.entity.Book;

import java.io.IOException;
import java.util.List;

/**
 * Exports books records as CSV file
 *
 * @return byte array representing the content of the CSV file
 * @author Atanas Atanasov
 * @version 1.0.0
 */
public class CsvFileExporter implements Exporter {

  private static final String CSV = "csv";
  private static final char COLUMN_SEPARATOR_CSV = ',';

  @Override
  public String getType() {
    return CSV;
  }

  @Override
  public byte[] export(List<Book> books) throws IOException {
    CsvMapper csvMapper = new CsvMapper();
    CsvSchema schema =
        csvMapper
            .schemaFor(Book.class)
            .withoutQuoteChar()
            .withHeader()
            .withColumnSeparator(COLUMN_SEPARATOR_CSV);

    return csvMapper.writer(schema).writeValueAsBytes(books);
  }
}
