package org.isaagents.isacreator.autofilteringlist;

import org.isaagents.isacreator.ontologybrowsingutils.TreeObserver;


public interface FilterSubject {

    public static final String UPDATE = "UPDATE";
    public static final String REMOVE = "REMOVE";

    public void registerObserver(FilterObserver observer);

    public void unregisterObserver(FilterObserver observer);

    public void notifyObservers(String observation);
}
