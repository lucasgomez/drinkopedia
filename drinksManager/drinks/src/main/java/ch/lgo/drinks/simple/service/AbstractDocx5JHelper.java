package ch.lgo.drinks.simple.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractDocx5JHelper {

	protected String buildFullName(String path, String baseFileName, String extension) {
		StringBuilder fullPath = new StringBuilder();
		if (StringUtils.isNotBlank(path)) {
			fullPath.append(path);
			if (!path.substring(path.length()-1, path.length()).contentEquals("/")) {
				fullPath.append("/");
			}
		}
		if (StringUtils.isNoneBlank(baseFileName)) {
			fullPath.append(baseFileName);
		} else {
			fullPath.append("phi");
		}
		fullPath.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("_yyyy-MM-dd_HH-mm-ss")));
		fullPath.append(extension);
		
		return fullPath.toString();
	}
	
	

}
