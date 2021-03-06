/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.core.thing;

import java.util.List;

/**
 * A {@link Bridge} is a {@link Thing} that connects other {@link Thing}s.
 * 
 * @author Dennis Nobel - Initial contribution and API
 */
public interface Bridge extends Thing {

    /**
     * Returns the children of the bridges.
     * 
     * @return children
     */
    List<Thing> getThings();

}
