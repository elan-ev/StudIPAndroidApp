/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
/**
 * 
 */
package studip.app.frontend.courses;

import studip.app.backend.datamodel.Event;
import studip.app.frontend.util.ArrayAdapterItem;
import android.widget.TextView;

/**
 * @author joern
 * 
 */
public class EventsItem implements ArrayAdapterItem {

    public Event event;

    public TextView titleTextView;

    public TextView startEndTextView;

    public EventsItem(Event event) {
	this.event = event;
    }

}
