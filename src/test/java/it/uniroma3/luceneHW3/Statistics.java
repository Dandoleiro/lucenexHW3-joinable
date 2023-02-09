package it.uniroma3.luceneHW3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class Statistics {

	private String jsonPath;
	private int numberOfTables;
	private int numberOfRows;
	private int numberOfColumns;
	private int numberOfNullValues;
	private	HashMap<Integer,Integer> distribuzioneRighe = new HashMap<Integer, Integer>();
	private	HashMap<Integer,Integer> distribuzioneColonne = new HashMap<Integer, Integer>();
	private HashMap<Integer,Integer> distribuzioneValoriDistinti = new HashMap<Integer,Integer>();
	private HashMap<String,Integer> referenceContext = new HashMap<String,Integer>();


	public Statistics(String jsonPath) {
		this.jsonPath = jsonPath;		
	}

	public void run() throws JsonSyntaxException, IOException {

		BufferedReader br = new BufferedReader(new FileReader(jsonPath));
		String line;

		BarraProgresso m=new BarraProgresso(0,550271);  
		m.setVisible(true);  
		m.setTitle("Caricamento Statistiche");  //Titolo della barra

		while ((line=br.readLine()) != null) {
			numberOfTables++;


			m.paint(m.getGraphics());  	//Aggiorna la tabella
			m.jb.setValue(numberOfTables);  //Imposta il nuovo valore. 

			JsonObject table = JsonParser.parseString(line).getAsJsonObject();
			JsonObject maxDimensions = table.get("maxDimensions").getAsJsonObject();
			numberOfRows += maxDimensions.get("row").getAsInt();
			numberOfColumns += maxDimensions.get("column").getAsInt();

			calcolareferenceContext(table);
			calcolaNumeroValoriNulli(table);
			calcolaDistribuzioneRigheColonne(table);
			calcolaDistribuzioneValoriDistinti(table);
			stampaStatistiche();


		}
		br.close();


		creaCSV();

	}

	public void stampaStatistiche() {
		System.out.println("Numero tot di tabelle: "+numberOfTables);
		System.out.println("Numero tot di colonne in tutte le tabelle: "+numberOfColumns);
		System.out.println("Numero tot di righe in tutte le tabelle: "+numberOfRows);
		System.out.println("Numero tot di valori nulli in tutte le tabelle: "+numberOfNullValues);
		System.out.println("Numero tot di referenceContext: "+this.referenceContext.size());

	}

	public void calcolareferenceContext(JsonObject table) {

		String Context = String.valueOf(table.get("referenceContext"));
		if(!this.referenceContext.containsKey(Context)) {
			this.referenceContext.put(Context, 1);
		}
		else {
			this.referenceContext.put(Context, this.referenceContext.get(Context)+1);
		}


	}

	public void calcolaDistribuzioneValoriDistinti(JsonObject table) {

		JsonArray celle = (JsonArray) table.get("cells");
		ArrayList<String> valoriDistintiPerTabella = new ArrayList<String>();

		for (Object c : celle) {
			JsonObject cella = (JsonObject) c;
			if(!(valoriDistintiPerTabella.contains(cella.get("cleanedText").getAsString()))) {  //se la lista non contiene quel valore
				valoriDistintiPerTabella.add(cella.get("cleanedText").getAsString());
			}
		}

		int dimensioneLista = valoriDistintiPerTabella.size();
		if(this.distribuzioneValoriDistinti.containsKey(dimensioneLista)) {
			this.distribuzioneValoriDistinti.put(dimensioneLista, this.distribuzioneValoriDistinti.get(dimensioneLista)+1);
		} else {
			this.distribuzioneValoriDistinti.put(dimensioneLista, 1);
		}
	}


	public void calcolaDistribuzioneRigheColonne( JsonObject table) {

		JsonObject maxDimensions = table.get("maxDimensions").getAsJsonObject();
		if(this.distribuzioneRighe.containsKey(maxDimensions.get("row").getAsInt())) {
			this.distribuzioneRighe.put(maxDimensions.get("row").getAsInt(), this.distribuzioneRighe.get(maxDimensions.get("row").getAsInt()) + 1);
		} else {
			this.distribuzioneRighe.put(maxDimensions.get("row").getAsInt(), 1);
		}
		if(this.distribuzioneColonne.containsKey(maxDimensions.get("column").getAsInt())) {
			this.distribuzioneColonne.put(maxDimensions.get("column").getAsInt(), this.distribuzioneColonne.get(maxDimensions.get("column").getAsInt()) + 1);
		} else {
			this.distribuzioneColonne.put(maxDimensions.get("column").getAsInt(), 1);
		}



	}

	public void calcolaNumeroValoriNulli(JsonObject table)  {

		JsonArray celle = (JsonArray) table.get("cells");

		for (Object c : celle) {
			JsonObject cella = (JsonObject) c;
			if(cella.get("cleanedText").getAsString().equals("")) {
				numberOfNullValues++;
			}
		}
	}

	public void creaCSV() {

		try (PrintWriter writer = new PrintWriter(new File("Resources/generalInformation.csv"))) {

			StringBuilder sb = new StringBuilder();
			sb.append("numberOfTables");
			sb.append(',');
			sb.append("numberOfRows");
			sb.append(',');
			sb.append("numberOfColumns");
			sb.append(',');
			sb.append("numberOfNullValues");
			sb.append('\n');

			sb.append(this.numberOfTables);
			sb.append(',');
			sb.append(this.numberOfRows);
			sb.append(',');
			sb.append(this.numberOfColumns);
			sb.append(',');
			sb.append(this.numberOfNullValues);
			sb.append('\n');

			writer.write(sb.toString());
			writer.close();
			System.out.println("generalInformation.csv done!");

		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		try (PrintWriter writer = new PrintWriter(new File("Resources/distribuzioneRighe.csv"))) {

			StringBuilder sb = new StringBuilder();
			sb.append("Numero valori righe");
			sb.append(',');
			sb.append("Quantità tabelle");
			sb.append('\n');



			for(Integer i : distribuzioneRighe.keySet()) {

				sb.append(i);
				sb.append(',');
				sb.append(this.distribuzioneRighe.get(i));
				sb.append('\n');

			}

			writer.write(sb.toString());
			writer.close();
			System.out.println("distribuzioneRighe.csv done!");

		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}

		try (PrintWriter writer = new PrintWriter(new File("Resources/distribuzioneColonne.csv"))) {

			StringBuilder sb = new StringBuilder();
			sb.append("Numero valori colonne");
			sb.append(',');
			sb.append("Quantità tabelle");
			sb.append('\n');



			for(Integer i : distribuzioneColonne.keySet()) {

				sb.append(i);
				sb.append(',');
				sb.append(this.distribuzioneColonne.get(i));
				sb.append('\n');

			}

			writer.write(sb.toString());
			writer.close();
			System.out.println("distribuzioneColonne.csv done!");

		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}

		try (PrintWriter writer = new PrintWriter(new File("Resources/distribuzioneValoriDistinti.csv"))) {

			StringBuilder sb = new StringBuilder();
			sb.append("Numero valori distinti per tabella");
			sb.append(',');
			sb.append("Quantità tabelle");
			sb.append('\n');



			for(Integer i : distribuzioneValoriDistinti.keySet()) {

				sb.append(i);
				sb.append(',');
				sb.append(this.distribuzioneValoriDistinti.get(i));
				sb.append('\n');

			}

			writer.write(sb.toString());
			writer.close();
			System.out.println("distribuzioneValoriDistinti.csv done!");

		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		
		try (PrintWriter writer = new PrintWriter(new File("Resources/distribuzioneReferenceContext.csv"))) {

			StringBuilder sb = new StringBuilder();
			sb.append("Oggetto di riferimento");
			sb.append(',');
			sb.append("Quantità tabelle con tale oggetto");
			sb.append('\n');



			for(String s : this.referenceContext.keySet()) {

				sb.append(s);
				sb.append(',');
				sb.append(this.referenceContext.get(s));
				sb.append('\n');

			}

			writer.write(sb.toString());
			writer.close();
			System.out.println("distribuzioneReferenceContext.csv done!");

		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}





	}
}
