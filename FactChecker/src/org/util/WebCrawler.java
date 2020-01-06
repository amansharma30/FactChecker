package org.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class WebCrawler {

	public static boolean scraping(String subject, String predicate, String object) throws IOException {

		URL url = new URL("https://en.wikipedia.org/w/index.php?title=" + subject.replace(" ", "_")); // hitting the url
		String text = "";
		try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()))) {
			String line = null;
			while (null != (line = br.readLine())) {
				line = line.trim();

				if (!line.startsWith("|") && !line.startsWith("{") && !line.startsWith("}") // neglecting commets while
																							// reading source content
						&& !line.startsWith("<center>") && !line.startsWith("---")) {
					text += line;
				}
			}

		}
		// mapping to simiilar words for predicate in wikipedia
		Map<String, String> lookUp = new HashMap<String, String>();
		lookUp.put("nascence place", "born");
		lookUp.put("birth place", "born");
		lookUp.put("innovation place", "headquarters");
		lookUp.put("death place", "died");
		lookUp.put("last place", "died");
		lookUp.put("better half", "spouse");
		lookUp.put("stars", "starring");
		lookUp.put("Actually stars", "starring");
		lookUp.put("nascence place", "born");
		lookUp.put("foundation place", "headquaters");

		if (lookUp.containsKey(predicate.toLowerCase().trim())) {
			predicate = lookUp.get(predicate.toLowerCase().trim());
		}

		String validText = "";
		text = text.toLowerCase().replaceAll("[^a-zA-Z0-9]", " ");
		object = object.toLowerCase().replaceAll("[^a-zA-Z0-9]", " ");
		if (text.toLowerCase().contains(predicate.toLowerCase().trim())) {

			int fromIndex = 0;
			while (text.toLowerCase().indexOf(predicate.toLowerCase().trim(), fromIndex) != -1) { // search for all
																									// occurences of
																									// predicate
				int indexPredicate = text.toLowerCase().indexOf(predicate.toLowerCase().trim(), fromIndex);

				// search for the object before the predicate

				validText += (String) text.subSequence(Math.max(indexPredicate - 1000, 0), indexPredicate) + " ";
				// search for the object after the predicate

				validText += (String) text.subSequence(indexPredicate, Math.min(indexPredicate + 1000, text.length()));
				if (validText.toLowerCase().contains(object.toLowerCase().trim())
						&& validText.toLowerCase().contains(predicate.toLowerCase().trim())) {
					return true;
				}

				fromIndex = text.toLowerCase().indexOf(predicate.toLowerCase().trim(), fromIndex)
						+ predicate.toLowerCase().trim().length() + 1;
			}

		}

		return false;

	}

	public static void main(String[] args) {
		try {
			System.out.println(WebCrawler.scraping("Last Action Hero  ", "stars", "Robert Prosky"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
