package com.knowledgebooks.rdf.implementation;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


/**
 * Wrapper class for an embedded Lucene index to support free text search of all
 * information sources that have been added.
 * <p/>
 * <p/>
 * Copyright 2002-2008 by Mark Watson. All rights reserved.
 * <p/>
 * This software is not public domain. It can be legally
 * used under either of the following licenses:
 * <p/>
 * 1. KnowledgeBooks.com Non Commercial Royality Free License<br/>
 * 2. KnowledgeBooks.com Commercial Use License
 * <p/>
 * see www.knowledgebooks.com for details
 */
public class LuceneRdfManager {
  private String data_store_file_root;

  /**
   * @param data_store_file_root
   * @throws java.lang.Exception
   */
  public LuceneRdfManager(String data_store_file_root) throws Exception {
    this.data_store_file_root = data_store_file_root;
  }

  /**
   * @throws java.io.IOException
   */
  public void close() throws IOException {
  }

  /**
   * @param subject
   * @param predicate
   * @param object
   * @throws org.apache.lucene.index.CorruptIndexException
   *
   * @throws java.io.IOException
   */
  public void addTripleToIndex(String subject, String predicate, String object) throws IOException {
    File index_dir = new File(data_store_file_root + "/lucene_index");
    writer = new IndexWriter(FSDirectory.open(index_dir), new StandardAnalyzer(Version.LUCENE_CURRENT), !index_dir.exists(), IndexWriter.MaxFieldLength.LIMITED);
    //File index_dir = new File(data_store_file_root + "/lucene_index");
    //writer = new IndexWriter(FSDirectory.open(index_dir), new StandardAnalyzer(Version.LUCENE_CURRENT), true, IndexWriter.MaxFieldLength.UNLIMITED);
    Document doc = new Document();
    doc.add(new Field("subject", subject, Field.Store.YES, Field.Index.NO));
    doc.add(new Field("predicate", predicate, Field.Store.YES, Field.Index.NO));
    doc.add(new Field("object", object, Field.Store.YES, Field.Index.ANALYZED));
    writer.addDocument(doc);
    writer.optimize();
    writer.close();
  }

  /**
   * @param search_query
   * @return
   * @throws org.apache.lucene.queryParser.ParseException
   *
   * @throws java.io.IOException
   */
  public List<List<String>> searchIndex(String search_query) throws ParseException, IOException {
    File index_dir = new File(data_store_file_root + "/lucene_index");
    reader = IndexReader.open(FSDirectory.open(index_dir), true);
    List<List<String>> ret = new ArrayList<List<String>>();
    Searcher searcher = new IndexSearcher(reader);

    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
    QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, "object", analyzer);
    Query query = parser.parse(search_query);

    TopScoreDocCollector collector = TopScoreDocCollector.create(10, false);
    searcher.search(query, collector);
    ScoreDoc[] hits = collector.topDocs().scoreDocs;

    for (int i = 0; i < hits.length; i += 1) {
      Document doc = searcher.doc(hits[i].doc);
      List<String> as2 = new ArrayList<String>(23);
      as2.add(doc.get("subject"));
      as2.add(doc.get("predicate"));
      as2.add(doc.get("object"));
      ret.add(as2);
    }
    reader.close();
    return ret;
  }

  private IndexWriter writer;
  private IndexReader reader;
}
