/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package studip.app.backend.net;

import studip.app.frontend.util.ArrayAdapterItem;
import android.app.Activity;
import android.widget.TextView;

public class ServerItem implements ArrayAdapterItem {

    public Server server;

    public Activity activity;

    public TextView tv;

    public ServerItem(Activity activity, Server server) {
	this.activity = activity;
	this.server = server;
    }

}
