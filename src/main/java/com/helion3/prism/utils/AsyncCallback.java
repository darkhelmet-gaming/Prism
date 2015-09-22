package com.helion3.prism.utils;

import java.util.List;

import com.helion3.prism.api.results.ResultRecord;

public class AsyncCallback {
    public void success(List<ResultRecord> results) {}

    public void empty() {}

    public void error(Exception e) {}
}
