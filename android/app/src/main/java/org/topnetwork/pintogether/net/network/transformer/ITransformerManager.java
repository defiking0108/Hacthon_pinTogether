package org.topnetwork.pintogether.net.network.transformer;

import io.reactivex.ObservableTransformer;

public interface ITransformerManager {

    <T> ObservableTransformer<T, T> apply();
}
