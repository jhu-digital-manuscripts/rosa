package rosa.core.util;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Helper to write XML.
 */

public class XMLWriter {
	private final TransformerHandler hd;
	private final AttributesImpl attr;
	private final static SAXTransformerFactory factory;
	private final Map<String,String> prefixMap; // namespaceURI -> prefix
    
	static {
		factory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
	}
        
	public XMLWriter(StreamResult out) {
		try {
			this.hd = factory.newTransformerHandler();
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		}
        
        this.prefixMap = new HashMap<String,String>();
		this.attr = new AttributesImpl();

		Transformer serializer = hd.getTransformer();
		serializer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
		serializer.setOutputProperty(OutputKeys.INDENT,"yes");

		hd.setResult(out);
	}
        
    public void startDocument() throws SAXException {
        hd.startDocument();
    }
    
    /**
     * Set up mapping of uri to prefix which will be used by other methods.
     * 
     * @param namespaceURI
     * @param prefix
     * @throws SAXException
     */
	public void namespace(String namespaceURI, String prefix) throws SAXException {
		prefixMap.put(namespaceURI, prefix);
		hd.startPrefixMapping(prefix, namespaceURI);
	}

    private String getQName(String namespaceURI, String name) throws SAXException {
    	if (namespaceURI == null) {
    		return name;
        }
        
        String prefix = prefixMap.get(namespaceURI);
        
        if (prefix == null) {
        	throw new SAXException("Unknown namespace " + namespaceURI);
        }
        
        return prefix + ':' + name;            
    }
    
	public void attribute(String name, String value) throws SAXException {
		attribute(null, name, value);
	}

	public void attribute(String namespaceURI, String name, String value) throws SAXException {
        String qname = getQName(namespaceURI, name);
        String type = "";
        
        if (namespaceURI == null) {
            namespaceURI = "";
        }
        
		attr.addAttribute(namespaceURI, name, qname, type, value);
	}
        
	public void text(String s) throws SAXException {
        char[] data = s.toCharArray();
        
		for (int i = 0; i < data.length; i++) {
			char c = data[i];
                
			// TODO not all java characters are valid xml characters
			// Turn non-whitespace ascii control characters into whitespace
			if (c < 32 && c != '\r' && c != '\n' && c != '\t') {
				data[i] = ' ';
			}
		}
		
		hd.characters(data, 0, data.length);
	}

	public void startElement(String namespaceURI, String name) throws SAXException {
        String qname = getQName(namespaceURI, name);

		if (namespaceURI == null) {
			namespaceURI = "";
		}
            
		hd.startElement(namespaceURI, name, qname, attr);
		attr.clear();
	}

	public void startElement(String name) throws SAXException {
		startElement(null, name);
	}
        
	public void endElement(String namespaceURI, String name) throws SAXException { 
        String qname = getQName(namespaceURI, name);

		if (namespaceURI == null) {
			namespaceURI = "";
		} 

		hd.endElement(namespaceURI, name, qname);
	}
        
	public void endElement(String name) throws SAXException {
		endElement(null, name);
	}
        
	public void element(String namespaceURI, String name, String value) throws SAXException {
		startElement(namespaceURI, name);
		text(value);
		endElement(namespaceURI, name);
	}
        
    public void emptyElement(String namespaceURI, String name) throws SAXException {
        startElement(namespaceURI, name);
        endElement(namespaceURI, name);
    }
    
    public void emptyElement(String name) throws SAXException {
        startElement(name);
        endElement(name);
    }
        
    public void endDocument() throws SAXException {
    	hd.endDocument();
    }
}


