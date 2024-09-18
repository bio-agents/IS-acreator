package org.isaagents.isacreator.ontologybrowsingutils;


public interface TreeSubject {
    public void registerObserver(TreeObserver observer);

    public void unregisterObserver(TreeObserver observer);

    public void notifyObservers();
}
