package make_database;

import java.io.IOException;  

import javax.xml.parsers.ParserConfigurationException;  
import javax.xml.parsers.SAXParser;  
import javax.xml.parsers.SAXParserFactory;  
  
import org.xml.sax.SAXException;
  
public class MyParser {  
  
    public static void main(String[] args) {  
        Long start = System.currentTimeMillis();  
        // sss  
        SAXParserFactory factory = SAXParserFactory.newInstance();  
        //ͨ��factory��ȡSAXParserʵ��  
        try {  
            SAXParser parser = factory.newSAXParser();  
            //����SAXParserHandler����  
            SAXParserHandler handler = new SAXParserHandler();  
            parser.parse(args[0], handler);  
        } catch (ParserConfigurationException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (SAXException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        Long end = System.currentTimeMillis();  
        System.out.println("Used: " + (end - start) / 1000 + " seconds");  
    }  
  
}  
