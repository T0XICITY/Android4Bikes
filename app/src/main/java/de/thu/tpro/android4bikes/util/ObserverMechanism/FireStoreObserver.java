package de.thu.tpro.android4bikes.util.ObserverMechanism;

public interface FireStoreObserver {
    void updateSuccessfullyStoredDocumentInFireStoreAndDB();

    void updateSuccessfullyDeletedDocumentFromFireStoreAndDB();

    void updateSuccessfullyReadDocumentFromFireStoreAndStoredInDB();

    void updateSuccessfullyUpdatedDocumentFromFireStoreAndDB();

    void failedToStore();

    void failedToDelete();

    void failedToRead();

    void failedToUpdate();
}
