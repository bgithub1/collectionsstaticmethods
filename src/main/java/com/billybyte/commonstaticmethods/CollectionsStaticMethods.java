package com.billybyte.commonstaticmethods;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import com.thoughtworks.xstream.XStream;

public class CollectionsStaticMethods {
	public static final String DEFAULT_CORRELATION_SEPARATOR = "__";
	public static final  <K,V> Map<K,V> mapInitFromArray(K[] ks,V... vs){
		return mapInitFromArray(new HashMap<K,V>(),ks,vs);
	}
	
	/**
	 * Return the map passed, but populated
	 * @param mapToInit
	 * @param ks
	 * @param vs
	 * @return
	 */
	public static final  <K,V> Map<K,V> mapInitFromArray(Map<K,V> mapToInit, K[] ks,V... vs){
		if(mapToInit==null){
			throw Utils.IllArg(CollectionsStaticMethods.class, " null map to initialize");
		}
		if(ks==null){
			throw Utils.IllArg(CollectionsStaticMethods.class, " null key array (ks)");
		}
		if(vs==null){
			throw Utils.IllArg(CollectionsStaticMethods.class, " null value array (vs)");
		}
		if(ks.length!=vs.length){
			throw Utils.IllArg(CollectionsStaticMethods.class, " lenth of vs ("+vs.length+") does not equal length ov ks ("+ks.length);
		}
		for(int i=0;i<vs.length;i++){
			mapToInit.put(ks[i], vs[i]);
		}
		return mapToInit;
	}
	
	public static final <E> List<E> listFromArray(E...es){
		return listFromArray(new ArrayList<E>(),es);
	}
	
	public static final <E> List<E> listFromArray(List<E> listToInit,E...es){
		if(listToInit==null){
			throw Utils.IllArg(CollectionsStaticMethods.class, " null list to initialize");
		}
		if(es==null){
			throw Utils.IllArg(CollectionsStaticMethods.class, " null es array");
		}
		for(E e:es){
			listToInit.add(e);
		}
		return listToInit;
	}
	
	public static final <E> Set<E> setFromArray(E...es){
		return setFromArray(new HashSet<E>(),es);
	}

	public static final <E> Set<E> setFromArray(Set<E> setToInit,E...es){
		if(setToInit==null){
			throw Utils.IllArg(CollectionsStaticMethods.class, " null list to initialize");
		}
		if(es==null){
			throw Utils.IllArg(CollectionsStaticMethods.class, " null es array");
		}
		for(E e:es){
			setToInit.add(e);
		}
		return setToInit;
	}
	

	/**
	 * Create a set of pairs, where the first part of the pair is
	 *   always alphabetically lower that the second part of the pair
	 * @param inputSet - set of individual items
	 * @param pairSeparator - string that separates pair set
	 * @return
	 */
	public static Set<String>  pairSetFromSet(
			Set<String> inputSet,
			String pairSeparator){
		// create an ordered list of hedges
		TreeSet<String> sortedHedgeSet = 
				new TreeSet<String>(inputSet);
		// make an array list of the hedges
		List<String> orderedList = new ArrayList<String>(sortedHedgeSet);
		// create a pairset
		Set<String> pairSet = new HashSet<String>();
		// build a pair set of this ordered list of hedges
		for(int i = 0;i<orderedList.size();i++){
			for(int j = i;j<orderedList.size();j++){
				String pair= orderedList.get(i)+pairSeparator+orderedList.get(j);
				pairSet.add(pair);
			}
		}
		return pairSet;
	}
	
	public static  <V> Map<String,V> replaceShortNameInPairMap(
			String existingPartialKey, 
			String replaceMentPartialKey,
			String separator,
			Map<String, V> mapToReplace){
		
		Map<String,V> ret = new HashMap<String, V>();
		for (Entry<String, V> entry : mapToReplace.entrySet()) {
			String key = entry.getKey();
			V value = entry.getValue();
			if(!key.contains(separator))continue;
			
			String newKey=key;
			if(key.contains(existingPartialKey)){
				String[] parts = key.split(separator);
				if(parts.length<2)continue;
				if(parts[0].compareTo(existingPartialKey)==0){
					newKey = replaceMentPartialKey+separator+parts[1];
					if(parts[1].compareTo(existingPartialKey)==0){
						newKey = replaceMentPartialKey+separator+replaceMentPartialKey;
					}
				}else if(parts[1].compareTo(existingPartialKey)==0){
					newKey = parts[0]+separator+replaceMentPartialKey;
				}
			}
			ret.put(newKey, value);
		}
		return ret;
	}
	
	/**
	 * Get a sub set of a map whose keys are "pairs" like AAPL.STK.SMART__IBM.STK.SMART
	 * 	All keys in the mapWithPairsAsKeys argument are "ordered" - i.e - 
	 * 			AAPL.STK.SMART__IBM.STK.SMART might be there, but
	 * 			IBM.STK.SMART__AAPL.STK.SMART will NOT be there.
	 * 
	 * @param snSet - Set<String> individual names from which you create pair keys 
	 * @param mapWithPairsAsKeys - Map<String,V> that contains values for each pair
	 * @param pairSeparator - String
	 * @return
	 */
	public static <V> Map<String, V> getSubPairMapFromStringSet(
			Set<String> snSet,
			Map<String,V> mapWithPairsAsKeys,
			String pairSeparator){
			String ps = pairSeparator;
			TreeMap<String, V> ret = new TreeMap<String, V>();
			List<String> orderedList = new ArrayList<String>(new TreeSet<String>(snSet));
			for(int i = 0;i<orderedList.size();i++ ){
				for(int j = 0;j<orderedList.size();j++){
					String pair = orderedList.get(i)+ps+orderedList.get(j);
					if(!mapWithPairsAsKeys.containsKey(pair))continue;
					ret.put(pair, mapWithPairsAsKeys.get(pair));
				}
			}
			
			return ret;
		}

	
	/**
	 * 
	 * @param keySet0 - initial key set
	 * @param keySet1 - key set which is sub set of keySet0
	 * @return - Set<K> of all elements in keySet0 that are NOT in keySet1
	 */
	public static <K> Set<K> getMissingKeys(Set<K> keySet0, Set<K> keySet1 ){
		Set<K> ret = new HashSet<K>();
		for(K key:keySet0){
			if(!keySet1.contains(key)){
				ret.add(key);
			}
		}
		return ret;
	}

	/**
	 * Get a collection that has been serialized into xml
	 * @param classInSamePackageAsCollectionFile
	 * @param pathOfCollection
	 * @return
	 */
	public static final Collection<?> getCollectionFromFile(
			Class<?> classInSamePackageAsCollectionFile,
			String pathOfCollection){
		return (Collection<?>)Utils.getFromXml(Collection.class, classInSamePackageAsCollectionFile, pathOfCollection);
	}
	
	public static <E> void  prtSetItems(Set<E> set){
		if(set==null || set.size()<1){
			Utils.prtObErrMess(CollectionsStaticMethods.class, "prtSetItems: can't print null set or set with no items");
			return;
		}
		for(E value :set){
			Utils.prt(value.toString());
		}
		
	}
	
	public static <K,V> void prtMapItems(Map<K, V> map){
		if(map==null || map.size()<1){
			Utils.prtObErrMess(CollectionsStaticMethods.class, "prtMapItems: can't print null map or map with no items");
		}
		for(Entry<K, V> entry:map.entrySet()){
			V v = entry.getValue();
			if(v.getClass().isArray()){
				Utils.prt(entry.getKey().toString()+","+ Arrays.toString((Object[])v));
			}else{
				Utils.prt(entry.getKey().toString()+","+v.toString());
			}
		}
	}

	public static <K,V> void prtMapItems(Map<K, V> map, LoggingUtils logger){
		if(map==null || map.size()<1){
			logger.error("prtMapItems: can't print null map or map with no items");
		}
		for(Entry<K, V> entry:map.entrySet()){
			V v = entry.getValue();
			if(v.getClass().isArray()){
				logger.info(entry.getKey().toString()+","+ Arrays.toString((Object[])v));
			}else{
				logger.info(entry.getKey().toString()+","+v.toString());
			}
		}
	}

	public static <K,V> String mapItemsToString(Map<K, V> map){
		if(map==null || map.size()<1){
			return "null";
		}
		String ret = "";
		for(Entry<K, V> entry:map.entrySet()){
			V v = entry.getValue();
			if(v.getClass().isArray()){
				 ret = ret + (entry.getKey().toString()+","+ Arrays.toString((Object[])v)) + "/n";
			}else{
				ret = ret + (entry.getKey().toString()+","+v.toString() + "/n");
			}
		}
		return ret;
	}
	
	public static void prtCsv(List<String[]>csv){
		for(String[] line:csv){
			Utils.prt(Arrays.toString(line));
		}

	}
	
	public static <E> void prtListItems(List<E> list){
		if(list==null || list.size()<1){
			Utils.prtObErrMess(CollectionsStaticMethods.class, "prtListItems: can't print null List or List with no items");
		}
		
		for(E element : list){
			if(Object[].class.isAssignableFrom(element.getClass())){
				Object[] oArr = (Object[])element;
				if(oArr.length>1){
					Utils.prt(Arrays.toString(oArr));
				}else{
					Utils.prt(element.toString());
				}
			}else{
				Utils.prt(element.toString());
			}
			//Utils.prt(element.toString());
		}
	}

	public static <E> void prtListItems(List<E> list, LoggingUtils logger){
		if(list==null || list.size()<1){
			logger.error("prtListItems: can't print null List or List with no items");
		}
		
		for(E element : list){
			if(Object[].class.isAssignableFrom(element.getClass())){
				Object[] oArr = (Object[])element;
				if(oArr.length>1){
					logger.info(Arrays.toString(oArr));
				}else{
					logger.info(element.toString());
				}
			}else{
				logger.info(element.toString());
			}
		}
	}

	public static Double[] fromPrim(double[] primArr){
		return ArrayUtils.toObject(primArr);
	}

	public static Integer[] fromPrim(int[] primArr){
		return ArrayUtils.toObject(primArr);
	}

	public static double[] toPrim(Double[] primArr){
		return ArrayUtils.toPrimitive(primArr);
	}

	public static int[] toPrim(Integer[] primArr){
		return ArrayUtils.toPrimitive(primArr);
	}
	
	public static final <E> String displayCleanToStringFromCollection(List<E> list){
        return list.toString().replaceAll("(\\[|\\])", "");
	}
	/**
	 * Create pairs of names from set of names for things like correlations
	 * @param shortNameSet
	 * @param separator
	 * @return
	 */
	public static 	Set<String> createCorrPairNames(Set<String> shortNameSet, String separator){
//		String sepToUse = separator==null?"__":separator;
		String sepToUse = separator==null?DEFAULT_CORRELATION_SEPARATOR:separator;
		TreeSet<String> ret = new TreeSet<String>();
		TreeSet<String> ordered = new TreeSet<String>(shortNameSet);
		for(String sn0:ordered){
			for(String sn1:ordered){
				if(sn0.compareTo(sn1)<=0){
					ret.add(sn0+sepToUse+sn1);
				}else{
					ret.add(sn1+sepToUse+sn0);
				}
			}
		}
		return ret;
	}

	public static <T> List<T> listFromCsv(Class<T> classOfItems,String csvFileName,
			Class<?> classInPkgOrResource) {
		List<String[]> csvData = 
				Utils.getCSVData(classInPkgOrResource, csvFileName);
		return listFromCsv(classOfItems, csvData);
	}
	
	
	/**
	 * 
	 * @param classOfItems Class<T>
	 * @param csvData List<String[]>
	 * @return List<T>
	 */
	public static <T> List<T> listFromCsv(Class<T> classOfItems,List<String[]> csvData) {
		try {
			if(csvData==null || csvData.size()<1)return null;
			String[] header = csvData.get(0);
			String listBeg = "<list>";
			String listEnd = "</list>";
			String clBeg = "<"+classOfItems.getCanonicalName()+">";
			String clEnd = "</"+classOfItems.getCanonicalName()+">";
			String xml=listBeg;
			
			for(int i =1;i<csvData.size();i++){
				String[] line = csvData.get(i);
				if(header.length>line.length)continue;
				xml = xml+clBeg;
				for(int j = 0;j<header.length;j++){
					String begToken = "<"+header[j].trim()+">";
					String endToken = "</"+header[j].trim()+">";
					String value = line[j].trim();
					xml = xml+begToken+value+endToken;
				}
				xml = xml+clEnd;
			}
			xml = xml+listEnd;
			XStream xs = new XStream();
			Object o = xs.fromXML(xml);
			if(!List.class.isAssignableFrom(o.getClass())){
				return null;
			}
			List<T> ret = new ArrayList<T>();

			@SuppressWarnings("rawtypes")
			List l = (List)o;
			if(l.size()<1){
//				return null;
				return ret;
			}
			Object lo = l.get(0);
			if(!classOfItems.isAssignableFrom(lo.getClass())){
				return null;
			}
			for(Object obj:l){
				T t = classOfItems.cast(obj);
				ret.add(t);
			}

			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <K,V> Map<K,V> mapFromCsv(
			Class<?> classInPackageOfFile,
			String csvFileName,
			String colNameOfKey,
			Class<K> classOfKey,
			String colNameOfData,
			Class<V> classOfData){
		
		List<String[]> csv = 
				classInPackageOfFile==null 
						? Utils.getCSVData(csvFileName) 
						: Utils.getCSVData(classInPackageOfFile, csvFileName);
		if(csv==null || csv.size()<2)return null;
		int colOfKey = 0;
		int firstRow = 0;
		if(colNameOfKey!=null){
			String[] header = csv.get(0);
			colOfKey = Utils.getCsvColumnIndex(colNameOfKey, header);
			if(colOfKey>-1){
				firstRow = 1;
			}
		}
		int colOfData = 1;
		if(colNameOfData!=null){
			String[] header = csv.get(0);
			int i = Utils.getCsvColumnIndex(colNameOfData, header);
			if(i > -1){
				colOfData = i;
			}
		}
		XStream xs = new XStream();
		String keyClassBegTag = "<"+classOfKey.getCanonicalName()+">";
		String keyClassEndTag = "</"+classOfKey.getCanonicalName()+">";
		String dataClassBegTag = "<"+classOfData.getCanonicalName()+">";
		String dataClassEndTag = "</"+classOfData.getCanonicalName()+">";
		
		Map<K,V> ret = new HashMap<K,V>();
		for(int i = firstRow;i<csv.size();i++){
			String keyString = keyClassBegTag +csv.get(i)[colOfKey] + keyClassEndTag;			
			String dataString = dataClassBegTag + csv.get(i)[colOfData] + dataClassEndTag;
			K key = (K)xs.fromXML(keyString);
			V value = (V)xs.fromXML(dataString);
			ret.put(key, value);
		}

		return ret;
	}
	
	/**
	 * make a map of objects of classOfData, where the key is the
	 *    colNameOfKey
	 *    
	 * @param csvNameOrPath
	 * @param classInPkgOfReource
	 * @param colNameOfKey
	 * @param classOfKey
	 * @param classOfData
	 */
	public static <K,V> Map<K,V> mapFromCsv(
			String csvNameOrPath,
			Class<?> classInPkgOfReource,
			String colNameOfKey,
			Class<K> classOfKey,
			Class<V> classOfData){
			
		// get csv data
		List<String[]> csvData = Utils.getCSVData(classInPkgOfReource, csvNameOrPath);
		// make them objects of V
		List<V> data = CollectionsStaticMethods.listFromCsv(classOfData, csvData);
		// new map
		Map<K,V> m = new HashMap<K, V>();
		// column of key
		int keyCol = Utils.getCsvColumnIndex(colNameOfKey, csvData.get(0));
		// string arg constructor for key
		Constructor<K> keyConstructorWithStringArg;
		try {
			keyConstructorWithStringArg = classOfKey.getConstructor(String.class);
		} catch (SecurityException e) {
			e.printStackTrace();
			throw Utils.IllState(e);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw Utils.IllState(e);
		}
		for(int i = 0;i<data.size();i++){
			V v = data.get(i); // get data
			String stringKey = csvData.get(i+1)[keyCol]; // get string version of key
			try {
				// convert key to type K
				K k = keyConstructorWithStringArg.newInstance(stringKey);
				m.put(k, v); // populate map
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				throw Utils.IllState(e);
			} catch (InstantiationException e) {
				e.printStackTrace();
				throw Utils.IllState(e);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				throw Utils.IllState(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw Utils.IllState(e);
			}
		}
		return m;
	}
 
	
	
	public static <T> Set<String> keySetFromCsv(
			Class<?> classInPackageOfFile,
			String csvFileName,
			String colNameOfKey){
		
		List<String[]> csv = 
				classInPackageOfFile==null 
						? Utils.getCSVData(csvFileName) 
						: Utils.getCSVData(classInPackageOfFile, csvFileName);
		if(csv==null || csv.size()<2)return null;
		int colNum = 0;
		int firstRow = 0;
		if(colNameOfKey!=null){
			String[] header = csv.get(0);
			colNum = Utils.getCsvColumnIndex(colNameOfKey, header);
			if(colNum>-1){
				firstRow = 1;
			}
		}
		Set<String> ret = new HashSet<String>();
		for(int i = firstRow;i<csv.size();i++){
			ret.add(csv.get(i)[colNum]);
		}
		return ret;
	}
	
	public static <K,V> Map<V,K> reverseMap(Map<K,V> sourceMap) {
		Map<V,K> revMap = new HashMap<V,K>();
		for(Entry<K,V> entry:sourceMap.entrySet()) {
			revMap.put(entry.getValue(), entry.getKey());
		}
		return revMap;
	}
	
	public static <K,V> Map<K,V> getSubMap(Set<K> keySet,Map<K,V> originalMap){
		Map<K, V> ret = new HashMap<K, V>();
		for(K key:keySet){
			if(!originalMap.containsKey(key))continue;
			ret.put(key,originalMap.get(key));
		}
		return ret;
	}

	public static final <T extends Comparable<T>> List<Set<T>> getSubSetList(
			Set<T> mainSet,
			int subSetSize){
		// make a list of subsets of missingShortNameSet
		List<T> all = 
				new ArrayList<T>(new TreeSet<T>(mainSet));
		List<Set<T>> subSetList = new ArrayList<Set<T>>();
		Set<T> currentSet = null;
		for(int i = 0;i<all.size();i++){
			if(i % subSetSize ==0){
				currentSet = new TreeSet<T>();
				subSetList.add(currentSet);
			}
			currentSet.add(all.get(i));
		}
		return subSetList;

	}

	
	public static <K1,K2,V> Map<K2, Map<K1,V>> reverse2DimMap(
			Map<K1,Map<K2,V>> mapToBeReversed){
		Map<K2, Map<K1,V>>  ret = new HashMap<K2, Map<K1,V>>();
		for(Entry<K1,Map<K2,V>> entry0:mapToBeReversed.entrySet()){
			K1 key1 = entry0.getKey();
			Map<K2,V> innerMap = entry0.getValue();
			for(Entry<K2,V> entry1:innerMap.entrySet()){
				V values = entry1.getValue();
				K2 key2 = entry1.getKey();
				if(!ret.containsKey(key2)){
					Map<K1,V> reversed = new HashMap<K1, V>();
					ret.put(key2,reversed);
				}
				Map<K1,V> reversed = ret.get(key2);
				reversed.put(key1, values);
			}
		}
		
		return ret;
		
	}

}
