package com.tapereader.reference;

import java.util.List;

import com.tapereader.clerk.Clerk;

public interface ReferenceDataClerk extends Clerk {

    List<Security> getSecurities();

}
