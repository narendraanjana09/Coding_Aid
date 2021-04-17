package com.nsa.CodingAid.ExtraClasses;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Firebase {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference_users,reference_fields;

    public Firebase() {
        firebaseDatabase=FirebaseDatabase.getInstance();
        reference_users=firebaseDatabase.getReference("users");
        reference_fields=firebaseDatabase.getReference("fields");
    }

    public FirebaseDatabase getFirebaseDatabase() {
        return firebaseDatabase;
    }

    public DatabaseReference getReference_users() {
        return reference_users;
    }

    public DatabaseReference getReference_fields() {
        return reference_fields;
    }
}
