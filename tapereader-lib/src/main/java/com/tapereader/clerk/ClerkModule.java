package com.tapereader.clerk;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class ClerkModule extends AbstractModule {
    
    @Override
    public void configure() {
        bind(RecordClerk.class).to(RecordClerkImpl.class);
        bind(LookupClerk.class).to(LookupClerkImpl.class);
    }
    
    @Provides
    public JPAClerk provideJPAClerk() {
        return JPAClerkImpl.INSTANCE;
    }

}
