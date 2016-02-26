//    Copyright (C) 2008  Emmanuel Nataf
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    	

/* 
 * Utility abstract class  to handle resources in concrete packages
 * Not for local support... Mostly for "configuration" stuff.
 */
package org.adtran.yang.parser.helpers;

import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.LogManager;

public  class Properties {
	public Properties(String baseName){
		try{
			this.baseName=baseName;
			this.configuration = ResourceBundle.getBundle(baseName);
		}catch (java.util.MissingResourceException e){
			// For Logger name we have a chicken-eggs problem 
				LogManager.getLogManager().getLogger("").logp(Level.SEVERE, 
													 this.getClass().getName(),
													 "Properties constructor",
													 "No "+baseName+" file has been found");
				//System.exit(-1); 
			}
	}
	
	public String getProperty(String property) {
		try {
			return configuration.getString(property);
		} catch (Exception e){
			LogManager.getLogManager().getLogger("").logp(Level.SEVERE, 
					 this.getClass().getName(),
					 "getProperty",
					 this.baseName+ ": Mandatory String property "+property+" is missing");
			return null;
		}
	}
	
	public String getOptionalProperty(String property, String byDefault) {
		try {
			return configuration.getString(property);
		}catch (Exception e){
			LogManager.getLogManager().getLogger("").logp(Level.WARNING, 
					 this.getClass().getName(),
					 "getOptionalProperty",
					 this.baseName+": Optional String property "+property+" not present");
			return byDefault;
		}
	}
	public String getOptionalProperty(String property) {
		return getOptionalProperty(property, null);
	}
	
	public int getIntProperty(String property){
		try{ 
			return Integer.parseInt(configuration.getString(property));
		}catch (NumberFormatException nfe){
			LogManager.getLogManager().getLogger("").logp(Level.SEVERE, 
					 this.getClass().getName(),
					 "getIntProperty",
					 this.baseName+": Mandatory integer property "+property+" as a bad format");
		}catch (Exception e){
			LogManager.getLogManager().getLogger("").logp(Level.SEVERE, 
					 this.getClass().getName(),
					 "getintProperty",
					 this.baseName+": Mandatory integer property "+property+" is missing");
		}
		return 0;
	}
	public int getOptionalIntProperty(String property, int byDefault){
		try{ 
			return Integer.parseInt(configuration.getString(property));
		}catch (NumberFormatException nfe){
			LogManager.getLogManager().getLogger("").logp(Level.SEVERE, 
					 this.getClass().getName(),
					 "getOptionalIntIntProperty",
					 this.baseName+": Optional integer property "+property+" as a bad format");
		}catch (Exception e){
			LogManager.getLogManager().getLogger("").logp(Level.WARNING, 
					 this.getClass().getName(),
					 "getOptionalIntProperty",
					 this.baseName+": Option integer property "+property+" not present");
		}
		return byDefault;
	}
	public boolean getBooleanProperty(String property){
		try{ 
			return Boolean.parseBoolean(configuration.getString(property));
		}catch (Exception e){
			LogManager.getLogManager().getLogger("").logp(Level.SEVERE, 
					 this.getClass().getName(),
					 "getBooleanProperty",
					 this.baseName+": Mandatory boolean property "+property+" is missing");
		}
		return false;
	}
	public boolean getOptinalBooleanProperty(String property, boolean byDefault){
		try{ 
			return Boolean.parseBoolean(configuration.getString(property));
		}catch (Exception e){
			LogManager.getLogManager().getLogger("").logp(Level.WARNING, 
					 this.getClass().getName(),
					 "getOptionalBooleanProperty",
					 this.baseName+": Optional boolean property "+property+" not present");
		}
		return byDefault;
	}
	public String dump(){
			String result="";
		for (Enumeration<String> keys=this.configuration.getKeys(); keys.hasMoreElements();){
			String k=(String)keys.nextElement();
			result=k+"="+this.configuration.getString(k);
		}
		return result;
	}

	private ResourceBundle configuration = null;
	private String baseName=null; // for logging
	
}
