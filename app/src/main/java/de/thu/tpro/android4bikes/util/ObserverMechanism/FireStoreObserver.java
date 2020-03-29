package de.thu.tpro.android4bikes.util.ObserverMechanism;

public interface FireStoreObserver {
    public void updateSuccessfullyStoredDocumentInFireStoreAndDB();
    public void updateSuccessfullyDeletedDocumentFromFireStoreAndDB();
    public void updateSuccessfullyReadDocumentFromFireStoreAndStoredInDB();
    public void updateSuccessfullyUpdatedDocumentFromFireStoreAndDB();

    public void failedToStore();
    public void failedToDelete();
    public void failedToRead();
    public void failedToUpdate();
}
