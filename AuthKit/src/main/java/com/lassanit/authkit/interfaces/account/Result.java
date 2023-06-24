package com.lassanit.authkit.interfaces.account;

import androidx.annotation.Nullable;

public interface Result {
    void onCompletion();
    void onCompletionInComplete();
    void onCompletionCancel(@Nullable Exception e);
}
