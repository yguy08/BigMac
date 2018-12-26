package com.tapereader.marketdata.historical;

import com.google.inject.AbstractModule;

public class NewspaperModule extends AbstractModule {
    
    @Override
    public void configure() {
        bind(Newspaper.class).to(NewspaperImpl.class);
    }

}
