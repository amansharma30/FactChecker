package org.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import java.util.List;

import java.util.stream.Collectors;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

public class UnSupervisedChecker {

	private String filePath;

	private List<Fact> factList;

	public static void main(String[] args) {
		List<String> fileList = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(new java.io.File("configurationFile.txt")))) {

			fileList = br.lines().collect(Collectors.toList());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println();
		UnSupervisedChecker checker = new UnSupervisedChecker();
		checker.checkFacts(fileList.get(0).split("=")[1]);
		checker.writeResults(fileList.get(1).split("=")[1]);
	}

	public List<Fact> checkFacts(String filePath) {
		this.filePath = filePath;
		List<String> list = new ArrayList<>();
		factList = new ArrayList<Fact>();
		try (BufferedReader br = new BufferedReader(new FileReader(new java.io.File(this.filePath)))) {

			list = br.lines().collect(Collectors.toList());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (String line : list) { // reading each fact line by line

			if (!line.startsWith("FactID")) {

				Fact fact = new Fact(Integer.valueOf(line.split("\t")[0]), line.split("\t")[1].trim(), 0.0);

				factList.add(fact);
				for (Sentence sent : new Document(fact.getFactString()).sentences()) {

					for (RelationTriple triple : sent.openieTriples()) {
						// Setting Subject ,Predicate and Object

						String subject = "";
						for (CoreLabel coreLabel : triple.subject) {
							subject += coreLabel.originalText() + " ";

						}

						String object = "";

						for (CoreLabel coreLabel : triple.object) {
							object += coreLabel.originalText() + " ";
						}

						String predicate = "";
						for (CoreLabel coreLabel : triple.relation) {
							predicate += coreLabel.originalText() + " ";
						}

						try {
							if (subject.contains("'")) {
								String[] parts = subject.split("'");
								subject = parts[0].trim();
								predicate = parts[1].replaceAll("s ", "").trim();
							} else if (object.contains("'")) {
								String temp_subject = subject;

								String[] parts = object.split("'");
								subject = parts[0].trim();
								predicate = parts[1].replaceAll("s ", "").trim();
								object = temp_subject;
							}

							if (!(predicate.contains("'") || predicate.contains("of") || predicate.contains("is")
									|| predicate.contains("in"))) {

								if (WebCrawler.scraping(subject.trim(), predicate.trim(), object.trim())) {
									fact.setFactValue(1.0);
									// break;
								} else {
									fact.setFactValue(-1.0);
								}
							}
							// System.out.println(subject + " ; predicate; " + predicate + " ;object ;" +
							// object);
							System.out.println("file processed " + fact.getFactId());
						} catch (IOException e) {
							System.err.println(subject + " not found");
							fact.setFactValue(0.0);
						}

					}
				}

			}
		}
		return factList;

	}

	public void writeResults(String filePath) {
		try {

			if (this.factList == null) {
				return;
			}

			File fout = new File(filePath);
			FileOutputStream fos = new FileOutputStream(fout);

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

			for (int i = 0; i < this.factList.size(); i++) {
				bw.write("<http://swc2017.aksw.org/task2/dataset/" + this.factList.get(i).getFactId()
						+ "> <http://swc2017.aksw.org/hasTruthValue> \"" + this.factList.get(i).getFactValue()
						+ "\"^^<http://www.w3.org/2001/XMLSchema#double> .");
				bw.newLine();
			}

			bw.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		System.out.println("file written at " + filePath);
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}
