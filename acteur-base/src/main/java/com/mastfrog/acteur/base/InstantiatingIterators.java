/* 
 * The MIT License
 *
 * Copyright 2013 Tim Boudreau.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.mastfrog.acteur.base;

import com.google.inject.Injector;
import com.mastfrog.util.collections.CollectionUtils;
import com.mastfrog.util.collections.Converter;
import java.util.List;
import javax.inject.Inject;

/**
 * Creates a typed iterator over a List&lt;Object&gt; which may contain either
 * objects of the given type, or class objects of subtypes of the type, in which
 * case they should be instantiated using Guice and the instance returned.
 *
 * @author Tim Boudreau
 */
final class InstantiatingIterators {

    private final Injector deps;

    @Inject
    InstantiatingIterators(Injector deps) {
        this.deps = deps;
    }

    <T> Iterable<T> iterable(List<Object> obj, final Class<T> type) {
        return CollectionUtils.<T>toIterable(CollectionUtils.<Object, T>convertedIterator(new ConverterImpl(type, deps), obj.iterator()));
    }

    private static class ConverterImpl<T> implements Converter<T, Object> {

        private final Class<T> type;
        private final Injector inj;

        public ConverterImpl(Class<T> type, Injector inj) {
            this.type = type;
            this.inj = inj;
        }

        @Override
        public Object unconvert(T r) {
            return r;
        }

        @Override
        public T convert(Object t) {
            if (t instanceof Class<?>) {
                return type.cast(inj.getInstance((Class<?>) t));
            } else {
                return type.cast(t);
            }
        }
    }

}