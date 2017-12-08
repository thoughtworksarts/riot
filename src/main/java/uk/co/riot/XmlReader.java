package uk.co.riot;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//https://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/

public class XmlReader {
	
	public static Config readXmlConfig(String path) {
		try {
			File fXmlFile = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			//optional, but recommended
			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();
			
			String videoFilePath = doc.getElementsByTagName("video").item(0).getTextContent();
			System.out.println(videoFilePath);
			String audioFilePath = doc.getElementsByTagName("audio").item(0).getTextContent();
			System.out.println(audioFilePath);
			
			NodeList levelNodes = doc.getElementsByTagName("level");
			
			List<Level> levels = new ArrayList<Level>();

			for (int temp = 0; temp < levelNodes.getLength(); temp++) {

				Node levelNode = levelNodes.item(temp);
				if (levelNode.getNodeType() == Node.ELEMENT_NODE) {
					Element levelElement = (Element) levelNode;
					
					String levelId = levelElement.getAttribute("id");
					
					SimpleDateFormat sdf = new SimpleDateFormat("mm:ss.SSS");
					sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
					
					String startString = levelElement.getElementsByTagName("start").item(0).getTextContent();
					long levelStart = sdf.parse(startString).getTime();
//					System.out.println("Start: " + levelStart);
					
//					String measureString = levelElement.getElementsByTagName("measure").item(0).getTextContent();
//					long levelMeasure = sdf.parse(measureString).getTime();
//					System.out.println("Measure: " + levelMeasure);
					
					String endString = levelElement.getElementsByTagName("end").item(0).getTextContent();
					long levelEnd = sdf.parse(endString).getTime();
//					System.out.println("End: " + levelEnd);
					
					ArrayList<Branch> branches = new ArrayList<Branch>();
					NodeList branchNodes = levelElement.getElementsByTagName("branch");
					for(int j = 0; j<branchNodes.getLength(); j++) {
						Node branchNode = branchNodes.item(j);
						if (branchNode.getNodeType() == Node.ELEMENT_NODE) {
							Element branchElement = (Element) branchNode;
							String branchId = branchElement.getAttribute("id");
							String branchStartString = branchElement.getElementsByTagName("start").item(0).getTextContent();
							long branchStart = sdf.parse(branchStartString).getTime();
							String branchEndString = branchElement.getElementsByTagName("end").item(0).getTextContent();
							long branchEnd = sdf.parse(branchEndString).getTime();
							String branchEmotion = branchElement.getElementsByTagName("emotion").item(0).getTextContent();
							String branchOutcome = branchElement.getElementsByTagName("outcome").item(0).getTextContent();
							branches.add(new Branch(branchId, branchStart, branchEnd, branchEmotion, branchOutcome));
						}
					}
					
					levels.add(new Level(levelId, levelStart, levelStart, levelEnd, branches));
				}				
			}
			
			return new Config(levels, videoFilePath, audioFilePath);
			
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
		return null;
	}
}
