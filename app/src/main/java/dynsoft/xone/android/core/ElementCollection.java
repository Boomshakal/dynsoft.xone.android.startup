package dynsoft.xone.android.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ElementCollection implements Collection<Element> {
	
	private Map<String, Element> _elements;
	
	public ElementCollection()
	{
		_elements = new LinkedHashMap<String, Element>();
	}
	
	public Element get(String code)
	{
	    if (code == null || code.length() == 0)
	        return null;
	    
	    if (_elements.containsKey(code)) {
	        return _elements.get(code);
	    }
	    
	    return Element.Empty;
	}

	@Override
	public boolean add(Element element) {
		if (element != null) {
			_elements.put(element.Code, element);
			return true;
		}
		
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends Element> elements) {
		if (elements != null) {
			for (Element element : elements) {
				_elements.put(element.Code, element);
			}
			return true;
		}
		return false;
	}

	@Override
	public void clear() {
		_elements.clear();
	}

	@Override
	public boolean contains(Object element) {
		return _elements.containsValue(element);
	}

	@Override
	public boolean containsAll(Collection<?> elements) {
		if (elements != null) {
			for (Object element : elements) {
				if (_elements.containsValue(element) == false) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean isEmpty() {
		return _elements.isEmpty();
	}

	@Override
	public Iterator<Element> iterator() {
		return _elements.values().iterator();
	}

	@Override
	public boolean remove(Object element) {
		if (element != null && element instanceof Element) {
			Element elmnt = (Element)element;
			Element rm = _elements.remove(elmnt);
			return rm != null;
		}
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> elements) {
		if (elements == null) return false;
		
		for (Object o : elements) {
		    _elements.remove(o);
		}
		
		return true;
	}

	@Override
	public boolean retainAll(Collection<?> elements) {
		return false;
	}

	@Override
	public int size() {
		return _elements.size();
	}

	@Override
	public Object[] toArray() {
		return _elements.values().toArray();
	}

	@Override
	public <T> T[] toArray(T[] elements) {
		return _elements.values().toArray(elements);
	}
}
