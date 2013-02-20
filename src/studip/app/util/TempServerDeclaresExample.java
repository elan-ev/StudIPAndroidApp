/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package studip.app.util;

import studip.app.backend.net.Server;

/**
 * TempServerDeclares Example file, only temporary until the serverList parser
 * is ready
 * 
 * @author joern
 * 
 */
public class TempServerDeclaresExample {
    public static Server alteTestumgebung = new Server("Examplename",
	    "exampleToken", "exampleSecret",
	    "http://exampleserver.domain/plugins.php/restipplugin");
}
