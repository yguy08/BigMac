package com.tapereader.reference;

import java.util.List;

import com.tapereader.clerk.Clerk;
import com.tapereader.model.Security;

public interface ReferenceDataClerk extends Clerk {

    List<Security> getSecurities();

}
