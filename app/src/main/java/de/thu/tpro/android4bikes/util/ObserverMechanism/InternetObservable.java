package de.thu.tpro.android4bikes.util.ObserverMechanism;

/**
 * notifies an observer whenever a internet connection is established.
 */
public interface InternetObservable {
    void notifyConnectionChange();

    void addObserver(InternetObserver observer);

    void removeObserver(InternetObserver observer);
}
