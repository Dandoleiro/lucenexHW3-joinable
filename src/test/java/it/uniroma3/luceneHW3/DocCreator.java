package it.uniroma3.luceneHW3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DocCreator {
	private IndexWriter indexWriter;
	private int tableCounter;
	private String docPath;
	private String jsonPath;

	public DocCreator(String docPath, String jsonPath) {
		this.docPath=docPath;
		this.jsonPath=jsonPath;
	}

	public void run() throws IOException {

		Path indexPath = Paths.get(this.docPath);
		try {
			Directory dir = FSDirectory.open(indexPath);
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			iwc.setCodec(new SimpleTextCodec());

			//Riscrivi la directory
			iwc.setOpenMode(OpenMode.CREATE);
			this.indexWriter = new IndexWriter(dir, iwc);
		} catch (Exception e) {
			System.err.println("Error opening the index. " + e.getMessage());
		}

		/**
		 * Inizializzo le variabili che mi servono per la lettura
		 */
		BufferedReader br = new BufferedReader(new FileReader(this.jsonPath));
		String line;
		this.tableCounter = 0;							

		BarraProgresso m=new BarraProgresso(0,550271);  
		m.setVisible(true);  
		m.setTitle("Caricamento Documenti");  //Titolo della barra		
		
		
		while ((line=br.readLine()) != null) {
			this.tableCounter++;
			

			m.paint(m.getGraphics());  	//Aggiorna la tabella
			m.jb.setValue(tableCounter);  //Imposta il nuovo valore. 
			
			
			JsonObject table = JsonParser.parseString(line).getAsJsonObject();	//faccio parse dell'oggetto
			String id = String.valueOf(table.get("id"));
			String referenceContext = String.valueOf(table.get("referenceContext"));
			Document doc = new Document();
			JsonArray celle = (JsonArray) table.get("cells");
			doc.add(new TextField("id", id, Field.Store.YES ));
			doc.add(new TextField("referenceContext", referenceContext, Field.Store.YES ));
			//System.out.println("Creato doc con id: " + id);
			String cleanedCells = "";

			for (Object c : celle) {
				JsonObject cella = (JsonObject) c;
				cleanedCells = cleanedCells.concat(cella.get("cleanedText").toString()+"\n");
			}

			doc.add(new TextField("keywords",cleanedCells ,Field.Store.YES));
			//System.out.println("Aggiunte al doc le celle con parole chiave " + cleanedCells);
			this.indexWriter.addDocument(doc);

			if (this.tableCounter % 100000 == 0) {
				System.out.println("Indicizzati " + this.tableCounter + " documenti.");
				this.indexWriter.commit();			// qui faccio il commit, provare anche a metterlo fuori dal ciclo
			}

		
		}

		br.close();

	

	}

}
